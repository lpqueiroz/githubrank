package controllers.handlers

import models.{ContributorsNotFoundException, RepoNotFoundException}
import play.api.mvc.Result
import play.api.mvc.Results.{InternalServerError, NoContent, NotFound}

trait ErrorHandlers {

  def exceptionHandlers: PartialFunction[Throwable, Result] = {
    case e: RepoNotFoundException => NotFound(e.getMessage)
    case _: ContributorsNotFoundException => NoContent
    case e: Exception => InternalServerError(e.getMessage)
  }

}
