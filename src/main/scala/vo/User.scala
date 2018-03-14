package vo

import org.joda.time.DateTime

case class User(id: UserId, info: UserView)

case class UserView(
  emailAddress: Seq[EmailAddress],
  sex: Sex,
  createdAt: DateTime
)
