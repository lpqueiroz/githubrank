package models

class GithubException(message: String) extends Exception(message)

case class RepoNotFoundException() extends GithubException(
  "Repo was not found."
)

case class ContributorsNotFoundException(organization: String) extends  GithubException(
  s"No Contributors found for Organization $organization"
)

