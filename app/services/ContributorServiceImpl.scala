package services

import models.{Contributor, ContributorsNotFoundException, RepoNotFoundException}
import repositories.GithubRepository
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContributorServiceImpl @Inject()(githubRepository: GithubRepository) extends ContributorService {

  override def listContributorsByOrganization(organizationName: String)
                                    (implicit ex: ExecutionContext): Future[Seq[Contributor]] = {
    for {
      reposFullName <- githubRepository.listReposNameByOrganization(organizationName)
      contributors <- if (reposFullName.isEmpty) Future.failed(RepoNotFoundException()) else Future.sequence(
        reposFullName.map(repoName => githubRepository.listContributorsByRepo(repoName))
      )
      contributorsWithAllContributions <- if (contributors.flatten.isEmpty) {
        Future.failed(ContributorsNotFoundException(organizationName))
      } else Future.successful(sumContributions(contributors.flatten))
    } yield contributorsWithAllContributions.sortBy(_.contributions).reverse
  }

  private def sumContributions(contributors: Seq[Contributor]): Seq[Contributor] = {
    contributors.groupBy(_.login).map {
      case (l, cont) => Contributor(l, cont.map(_.contributions).sum)
    }.toSeq
  }
}
