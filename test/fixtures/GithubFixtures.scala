package fixtures

import models.Contributor
import models.types.FullNameRepo
import repositories.GithubRepository
import services.{ContributorService, ContributorServiceImpl}

import javax.naming.ldap.Control
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

object GithubFixtures {

  def contributorTestService(fullNameRepo: ArrayBuffer[FullNameRepo] = ArrayBuffer.empty,
                             contributors: ArrayBuffer[Contributor] = ArrayBuffer.empty): ContributorServiceImpl = {
    new ContributorServiceImpl(
      githubTestRepository(fullNameRepo, contributors)
    )
  }

  def githubTestRepository(fullNameRepo: ArrayBuffer[FullNameRepo] = ArrayBuffer.empty,
                           contributors: ArrayBuffer[Contributor] = ArrayBuffer.empty) = {
    new GithubRepository {
      override def listReposNameByOrganization(organization: String)
                                              (implicit ex: ExecutionContext): Future[Seq[FullNameRepo]] =
        Future.successful(fullNameRepo.toSeq)

      override def listContributorsByRepo(fullNameRepo: FullNameRepo)
                                         (implicit ex: ExecutionContext): Future[Seq[Contributor]] =
        Future.successful(contributors.toSeq)
    }
  }

  def contributorStubService(
                              result: Future[Seq[Contributor]]
                            ): ContributorService = new ContributorService {
    override def listContributorsByOrganization(organizationName: String)
                                               (implicit ex: ExecutionContext): Future[Seq[Contributor]] = result
  }

}
