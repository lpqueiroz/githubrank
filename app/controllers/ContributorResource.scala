package controllers

import models.Contributor
import play.api.libs.json.{JsObject, Json, Writes}

object ContributorResource {

  implicit val writesContributor = new Writes[Contributor] {
    def writes(contributor: Contributor): JsObject = Json.obj(
      "name" -> contributor.login,
      "contributions" -> contributor.contributions
    )
  }

  implicit val readsContributor = Json.reads[Contributor]

}
