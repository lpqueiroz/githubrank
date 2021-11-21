package repositories

import com.typesafe.config.Config
import controllers.ContributorResource._
import models.types.FullNameRepo
import models.Contributor
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient
import utils.DataTransformationUtils
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

class GithubRepositoryImpl @Inject()(ws: WSClient, config: Config) extends GithubRepository {

  private def token = config.getString("accessToken")

  private def baseURL() = "https://api.github.com/"

  // TODO: Alternative solution to get only first page of the repos
//    override def listReposNameByOrganization(organization: String)
//                                            (implicit ex: ExecutionContext): Future[Seq[FullNameRepo]] = {
//      val url = baseURL() + s"orgs/${organization}/repos?per_page=100"
//
//      for {
//        response <- ws.url(url).withHttpHeaders("authorization" -> token).get()
//        repos <- Future.fromTry(
//          Try(
//            (response.json \\ "full_name").map(_.as[FullNameRepo])
//          ).recoverWith {
//            case e => Failure(e)
//          })
//      } yield repos.toSeq
//    }

  override def listReposNameByOrganization(organization: String)
                                          (implicit ex: ExecutionContext): Future[Seq[FullNameRepo]] = {
    val url = baseURL() + s"orgs/${organization}/repos?per_page=100"

    for {
      response <- ws.url(url).withHttpHeaders("authorization" -> token).get()
      link = response.headerValues("Link")
      responsesFromAllPages <- if (link.nonEmpty)
        requestPaginated(DataTransformationUtils.parse(link.toIndexedSeq), Seq(response.json))
      else Future.successful(Seq(response.json))
      repos <- Future.fromTry(
        Try(
          responsesFromAllPages.map(jsvalue => (jsvalue \\ "full_name").map(_.as[FullNameRepo]))
        ).recoverWith {
        case e => Failure(e)
      }).map(_.flatten)
    } yield repos
  }

  override def listContributorsByRepo(fullNameRepo: FullNameRepo)
                                     (implicit ex: ExecutionContext): Future[Seq[Contributor]] = {
    val url = baseURL() + s"repos/${fullNameRepo}/contributors?per_page=100"
    for {
      response <- ws.url(url).withHttpHeaders("authorization" -> token).get()
      link = response.headerValues("Link")
      responsesFromAllPages <- if (link.nonEmpty)
        requestPaginated(DataTransformationUtils.parse(link.toIndexedSeq), Seq(response.json))
      else Future.successful(Seq.empty)
      contributors <- Future.fromTry(
        Try(
          (responsesFromAllPages.map(_.as[Seq[Contributor]]))
        ).recoverWith {
          case e => Failure(e)
        }).map(_.flatten)
    } yield contributors
  }

  private def requestPaginated(linkRel: Map[String, String], acc: Seq[JsValue])
                      (implicit ex: ExecutionContext): Future[Seq[JsValue]] = {
    Future.sequence {
      linkRel.map {
        case (next, value) => if (next == "next") {
          for {
            response <- ws.url(value).withHttpHeaders("authorization" -> token).get()
            link = DataTransformationUtils.parse(response.headerValues("Link").toIndexedSeq)
            req <- requestPaginated(link, acc :+ response.json)
          } yield req
        } else Future.successful(acc)
      }.toSeq
    }.map(_.flatten)
  }
}
