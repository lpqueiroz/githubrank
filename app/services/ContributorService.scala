package services

import com.google.inject.ImplementedBy
import models.Contributor

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[ContributorServiceImpl])
trait ContributorService {

  def listContributorsByOrganization(organizationName: String)
                                    (implicit ex: ExecutionContext): Future[Seq[Contributor]]

}
