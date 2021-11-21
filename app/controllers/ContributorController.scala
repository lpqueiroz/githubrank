package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import services.ContributorService

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import play.api.libs.json.Json
import ContributorResource._
import controllers.handlers.ErrorHandlers

class ContributorController @Inject()(contributorService: ContributorService,
                                      val controllerComponents: ControllerComponents
                                     )
                                     (implicit ex: ExecutionContext) extends BaseController with ErrorHandlers {

  def listContributorsByOrganization(organization: String): Action[AnyContent] = Action.async {
    implicit request =>
      contributorService.listContributorsByOrganization(organization)
        .map(
          contributors => Ok(Json.toJson(contributors))
        ).recover(exceptionHandlers)
  }
}
