package converter

import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.{JsValue, Json}
import testobject.instance.constant.ConstantTestObject
import testobject.instance.deterministic.DeterministicTestObject
import vo.User

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
            |  "id" : "string (0)",
            |  "info" : {
            |    "email_address" : ["string (1)", "string (2)", "string (3)"],
            |    "sex" : "Unknown(string (4))",
            |    "created_at" : "2018-03-18T00:00:00.000+09:00"
            |  }
            |}
          """.stripMargin
        ),
        Json.parse(
          """
            |{
            |  "id" : "string (6)",
            |  "info" : {
            |    "email_address" : ["string (7)", "string (8)", "string (9)"],
            |    "sex" : "Unknown(string (10))",
            |    "created_at" : "2018-03-24T00:00:00.000+09:00"
            |  }
            |}
          """.stripMargin
        )
      )

      someUsers zip expectedSeq foreach {
        case (user, expected) =>
          val actual = sut.toJson(user)
          actual must be(expected)
      }
    }
  }
}
