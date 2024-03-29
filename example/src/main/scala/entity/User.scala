package entity

import java.time.ZonedDateTime

case class User(id: UserId, info: UserView)

case class UserId(value: String)

case class UserView(
  emailAddress: Seq[EmailAddress],
  sex: Sex,
  age: Age,
  createdAt: ZonedDateTime
)
