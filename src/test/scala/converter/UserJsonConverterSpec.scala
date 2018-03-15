package converter

import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.{JsValue, Json}
import testobject.instance.constant.ConstantTestObject
import testobject.instance.deterministic.DeterministicTestObject
import entity.User

class UserJsonConverterSpec extends WordSpec with MustMatchers {
  trait SetUp {
    val sut = new UserJsonConverter
  }

  "toJson" should {
    "return a constant JSON successfully" in new SetUp {
      val constantUser: User = ConstantTestObject[User]

      val expected: JsValue = Json.parse(
        """
          |{
          |  "id" : "string",
          |  "info" : {
          |    "email_address" : ["string", "string", "string"],
          |    "sex" : "female",
          |    "age" : 123,
          |    "created_at" : "2018-03-13T00:00:00.000+09:00"
          |  }
          |}
        """.stripMargin
      )

      val actual = sut.toJson(constantUser)

      actual must be(expected)
    }

    "return some JSONs successfully" in new SetUp {
      val someUsers: Seq[User] = (for {
        u1 <- DeterministicTestObject[User]
        u2 <- DeterministicTestObject[User]
      } yield Seq(u1, u2)).apply(0)._2

      val expectedSeq: Seq[JsValue] = Seq(
        Json.parse(
          """
            |{
            |  "id" : "0",
            |  "info" : {
            |    "email_address" : ["1", "2", "3"],
            |    "sex" : "Unknown(4)",
            |    "age" : 5,
            |    "created_at" : "2018-03-19T00:00:00.000+09:00"
            |  }
            |}
          """.stripMargin
        ),
        Json.parse(
          """
            |{
            |  "id" : "7",
            |  "info" : {
            |    "email_address" : ["8", "9", "10"],
            |    "sex" : "male",
            |    "age" : 11,
            |    "created_at" : "2018-03-25T00:00:00.000+09:00"
            |  }
            |}
          """.stripMargin
        )
      )

      (someUsers zip expectedSeq) foreach {
        case (user, expected) =>
          val actual = sut.toJson(user)
          actual must be(expected)
      }
    }
  }
}
