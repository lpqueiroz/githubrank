package services

import fixtures.GithubFixtures
import models.{Contributor, ContributorsNotFoundException, RepoNotFoundException}
import models.types.FullNameRepo
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import repositories.GithubRepository

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{ExecutionContext, Future}

class ContributorServiceSpec(implicit ev: ExecutionEnv) extends Specification {

  private def serviceWithRepoReturningException() = new ContributorServiceImpl(
    new GithubRepository {
      override def listReposNameByOrganization(organization: String)
                                              (implicit ex: ExecutionContext): Future[Seq[FullNameRepo]] =
        Future.failed(new Exception())

      override def listContributorsByRepo(fullNameRepo: FullNameRepo)
                                         (implicit ex: ExecutionContext): Future[Seq[Contributor]] =
        Future.failed(new Exception())
    }
  )

  "This is the specification of ContributorService" >> {
    "listContributorsByOrganization" >> {
      "must throw an exception" >> {
        "if some exception is thrown in the repository" >> {
          val result = serviceWithRepoReturningException()
            .listContributorsByOrganization("test")

          result must throwA[Exception].await
        }
      }
      "must return a sequence of contributors" >> {
        "if no exception is thrown by the service" >> {
          val contributor = ArrayBuffer(
            Contributor("lqueiroz", 32),
            Contributor("fakeUser", 11),
            Contributor("fakeUser2", 40)
          )
          val fullNameRepo = ArrayBuffer("github")
          val service = GithubFixtures.contributorTestService(fullNameRepo, contributor)

          val result = service.listContributorsByOrganization("fakeOrganization")

          result must beEqualTo(
            Seq(Contributor("fakeUser2", 40), Contributor("lqueiroz", 32), Contributor("fakeUser", 11))
          ).await
        }
      }
      "must return RepoNotFoundException" >> {
        "if any repo was found" >> {
          val contributor = ArrayBuffer(
            Contributor("lqueiroz", 32),
            Contributor("fakeUser", 11),
            Contributor("fakeUser2", 40)
          )
          val service = GithubFixtures.contributorTestService(contributors = contributor)

          val result = service.listContributorsByOrganization("fakeOrganization")

          result must throwA[RepoNotFoundException].await
        }
      }
      "must return ContributorsNotFoundException" >> {
        "if any contributor was found for the organization" >> {
          val fullNameRepository = ArrayBuffer("github")
          val service = GithubFixtures.contributorTestService(fullNameRepo = fullNameRepository)

          val result = service.listContributorsByOrganization("fakeOrganization")

          result must throwA[ContributorsNotFoundException].await
        }
      }
    }
  }
}
