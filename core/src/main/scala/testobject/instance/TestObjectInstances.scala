package testobject.instance

import cats.data.State
import testobject.TestObject
import java.time.ZonedDateTime
import java.util.TimeZone

trait TestObjectInstances extends TestObjectGenericInstances {
  type IntState[A] = State[Int, A]

  implicit val testString: TestObject[String] = new TestObject[String] {
    def generate: IntState[String] = State(s => (s + 1, s.toString))
  }

  implicit val testInt: TestObject[Int] = new TestObject[Int] {
    def generate: IntState[Int] = State(s => (s + 1, s))
  }

  implicit val testLong: TestObject[Long] = new TestObject[Long] {
    def generate: IntState[Long] = State(s => (s + 1, s))
  }

  implicit val testDouble: TestObject[Double] = new TestObject[Double] {
    def generate: IntState[Double] = State(s => (s + 1, 0.1 * s))
  }

  implicit val testBoolean: TestObject[Boolean] = new TestObject[Boolean] {
    def generate: IntState[Boolean] = State(s => (s + 1, s % 2 == 0))
  }

  implicit val testZonedDateTime: TestObject[ZonedDateTime] = new TestObject[ZonedDateTime] {
    def generate: IntState[ZonedDateTime] =
      State(s =>
        (s + 1, ZonedDateTime.of(2018, 3, 13, 0, 0, 0, 0, TimeZone.getTimeZone("Asia/Tokyo").toZoneId).plusDays(s))
      )
  }

  implicit def testOption[A: TestObject]: TestObject[Option[A]] =
    new TestObject[Option[A]] {
      def generate: IntState[Option[A]] =
        for {
          a <- implicitly[TestObject[A]].generate
          s <- State.get
        } yield
          if (s % 2 == 0)
            Some(a)
          else
            None
    }

  implicit def testSeq[A: TestObject]: TestObject[Seq[A]] =
    new TestObject[Seq[A]] {
      override def generate: IntState[Seq[A]] =
        for {
          one <- implicitly[TestObject[A]].generate
          two <- implicitly[TestObject[A]].generate
          three <- implicitly[TestObject[A]].generate
        } yield Seq(one, two, three)
    }

  implicit def testSet[A: TestObject]: TestObject[Set[A]] =
    new TestObject[Set[A]] {
      override def generate: IntState[Set[A]] =
        for {
          one <- implicitly[TestObject[A]].generate
          two <- implicitly[TestObject[A]].generate
          three <- implicitly[TestObject[A]].generate
        } yield Set(one, two, three)
    }

  implicit val testObjectCatsInstance: cats.Monad[TestObject] =
    new cats.Monad[TestObject] {
      override def pure[A](x: A): TestObject[A] = new TestObject[A] {
        override def generate: State[Int, A] = State((_, x))
      }

      override def map[A, B](fa: TestObject[A])(f: A => B): TestObject[B] =
        new TestObject[B] {
          override def generate: State[Int, B] = fa.generate.map(f)
        }

      override def flatMap[A, B](fa: TestObject[A])(
        f: A => TestObject[B]
      ): TestObject[B] =
        new TestObject[B] {
          override def generate: State[Int, B] =
            fa.generate.flatMap(a => f(a).generate)
        }

      override def tailRecM[A, B](a: A)(f: A => TestObject[Either[A, B]]): TestObject[B] = new TestObject[B] {
        override def generate: State[Int, B] =
          cats.Monad[State[Int, *]].tailRecM[A, B](a) { a =>
            f(a).generate
          }
      }
    }
}
