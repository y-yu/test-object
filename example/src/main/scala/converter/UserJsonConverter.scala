package converter

import entity.*
import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class UserJsonConverter {
  def toJson(user: User): JsValue = {
    import UserJsonConverter.*
    Json.toJson(user)
  }

  def prettyPrint(user: User): String =
    Json.prettyPrint(toJson(user))
}

object UserJsonConverter {
  implicit val datetimeFormat: Writes[ZonedDateTime] =
    Writes(datetime => JsString(datetime.format(DateTimeFormatter.ISO_DATE_TIME)))
  implicit val userIdWrites: Writes[UserId] = Writes(id => JsString(id.value))
  implicit val userViewWrites: Writes[UserView] = (
    (__ \ "email_address").write[Seq[String]].contramap[Seq[EmailAddress]](_.map(_.value)) and
      (__ \ "sex").write[String].contramap[Sex](_.value) and
      (__ \ "age").write[Int].contramap[Age](_.value) and
      (__ \ "created_at").write[ZonedDateTime]
  )(userView => (userView.emailAddress, userView.sex, userView.age, userView.createdAt))
  implicit val userWrites: Writes[User] = Json.writes[User]
}
