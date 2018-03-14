import converter.UserJsonConverter
import org.joda.time.DateTime
import vo._

object Main {
  def main(args: Array[String]): Unit = {
    val user = User(
      id = UserId("2"),
      info = UserView(
        emailAddress = Seq(EmailAddress("a@example.com"), EmailAddress("b@example.com")),
        sex = Male,
        createdAt = new DateTime("2018-03-15")
      )
    )

    val converter = new UserJsonConverter

    println(converter.prettyPrint(user))
  }
}
