package converter

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import entity.User
import org.scalatest.wordspec.AnyWordSpec
import testobject.TestObject

class UserJsonConverterSpec extends AnyWordSpec {
  trait SetUp {
    val sut = new UserJsonConverter
  }

  "toJson" should {
    "return some JSONs successfully" in new SetUp {
      val someUsers: Seq[User] = (for {
        u1 <- TestObject[User]
        u2 <- TestObject[User]
      } yield Seq(u1, u2)).eval

      val expectedSeq: Seq[JsValue] = Seq(
        Json.parse(
          """
            |{
            |  "id": "0",
            |  "info": {
            |    "email_address": [
            |      "1",
            |      "2",
            |      "3"
            |    ],
            |    "sex": "Unknown(4)",
            |    "age": 5,
            |    "created_at": "2018-03-19T00:00:00+09:00[Asia/Tokyo]"
            |  }
            |}
            |""".stripMargin
        ),
        Json.parse(
          """
            |{
            |  "id": "7",
            |  "info": {
            |    "email_address": [
            |      "8",
            |      "9",
            |      "10"
            |    ],
            |    "sex": "female",
            |    "age": 12,
            |    "created_at": "2018-03-26T00:00:00+09:00[Asia/Tokyo]"
            |  }
            |}
            |""".stripMargin
        )
      )

      (someUsers zip expectedSeq) foreach { case (user, expected) =>
        val actual = sut.toJson(user)
        assert(actual === expected)
      }
    }
  }
}
