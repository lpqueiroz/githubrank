package repositories

import com.google.inject.ImplementedBy
import models.Contributor
import models.types.FullNameRepo
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[GithubRepositoryImpl])
trait GithubRepository {

  def listReposNameByOrganization(organization: String)
                                 (implicit ex: ExecutionContext): Future[Seq[FullNameRepo]]

  def listContributorsByRepo(fullNameRepo: FullNameRepo)
                            (implicit ex: ExecutionContext): Future[Seq[Contributor]]

}
