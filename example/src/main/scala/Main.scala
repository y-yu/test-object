import converter.UserJsonConverter
import entity.*
import java.time.ZoneId
import java.time.ZonedDateTime

object Main {
  def main(args: Array[String]): Unit = {
    val user = User(
      id = UserId("2"),
      info = UserView(
        emailAddress = Seq(EmailAddress("a@example.com"), EmailAddress("b@example.com")),
        sex = Male,
        age = Age(20),
        createdAt = ZonedDateTime.of(2018, 3, 15, 0, 0, 0, 0, ZoneId.of("Asia/Tokyo"))
      )
    )

    val converter = new UserJsonConverter

    println(converter.prettyPrint(user))
  }
}
