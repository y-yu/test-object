package converter

import org.joda.time.DateTime
import entity._
import play.api.libs.functional.syntax._
import play.api.libs.json._

class UserJsonConverter {
  def toJson(user: User): JsValue = {
    import UserJsonConverter._
    Json.toJson(user)
  }

  def prettyPrint(user: User): String =
    Json.prettyPrint(toJson(user))
}

object UserJsonConverter {
  implicit val datetimeFormat: Writes[DateTime] = JodaWrites.JodaDateTimeWrites
  implicit val userIdWrites: Writes[UserId] = Writes(id => JsString(id.value))
  implicit val userViewWrites: Writes[UserView] = (
      (__ \ "email_address").write[Seq[String]].contramap[Seq[EmailAddress]](_.map(_.value)) and
      (__ \ "sex").write[String].contramap[Sex](_.value) and
      (__ \ "age").write[Int].contramap[Age](_.value) and
      (__ \ "created_at").write[DateTime]
    )(unlift(UserView.unapply))
  implicit val userWrites: Writes[User] = Json.writes[User]
}
