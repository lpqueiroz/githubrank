package controllers

import fixtures.GithubFixtures
import models.{Contributor, ContributorsNotFoundException, RepoNotFoundException}
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

class ContributorControllerSpec(implicit ev: ExecutionEnv) extends Specification {

  case class TestData(contributors: ArrayBuffer[Contributor] = ArrayBuffer.empty,
                      successOrFailure: Future[Seq[Contributor]] = Future.failed(new Exception()))

  def contributorController(_testData: TestData) = new ContributorController(
    GithubFixtures.contributorStubService(_testData.successOrFailure),
    stubControllerComponents()
  )

  "This is the specification of ContributorController" >> {
    "listContributorsByOrganization" >> {
      "if no exception returns from service" >> {
        "must return 200 OK" >> {
          val contributor = Contributor("lqueiroz", 30)
          val controller = contributorController(TestData(ArrayBuffer(contributor), Future.successful(Seq(contributor))))

          val request = FakeRequest("GET", "fakeuri")

          val result: Future[Result] = controller.listContributorsByOrganization("fakeOrganization")(request)

          status(result) must beEqualTo(200)
        }
      }
      "if an exception returns from service" >> {
        "must return 500 INTERNAL SERVER ERROR" >> {
          val contributor = Contributor("lqueiroz", 30)
          val controller = contributorController(
            TestData(ArrayBuffer(contributor), Future.failed(new Exception("random exception")))
          )

          val request = FakeRequest("GET", "fakeuri")

          val result: Future[Result] = controller.listContributorsByOrganization("fakeOrganization")(request)

          status(result) must beEqualTo(500)
        }
      }
      "if any repository for the organization was found" >> {
        "must return 404 NOT FOUND" >> {
          val contributor = Contributor("lqueiroz", 30)
          val controller = contributorController(
            TestData(ArrayBuffer(contributor), Future.failed(RepoNotFoundException()))
          )

          val request = FakeRequest("GET", "fakeuri")

          val result: Future[Result] = controller.listContributorsByOrganization("fakeOrganization")(request)

          status(result) must beEqualTo(404)
        }
      }
      "if any contributor for the organization was found" >> {
        "must return 204 NO CONTENT" >> {
          val contributor = Contributor("lqueiroz", 30)
          val controller = contributorController(
            TestData(ArrayBuffer(contributor), Future.failed(ContributorsNotFoundException("fakeOrganization")))
          )

          val request = FakeRequest("GET", "fakeuri")

          val result: Future[Result] = controller.listContributorsByOrganization("fakeOrganization")(request)

          status(result) must beEqualTo(204)
        }
      }
    }
  }

}
