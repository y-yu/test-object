package testobject.instance.deterministic

import org.joda.time.DateTime
import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr, Lazy}
import testobject.TestObject
import testobject.instance.AbstractTestObject
import scalaz.State

trait DeterministicTestObject[A] extends TestObject[Int, A]

object DeterministicTestObject extends AbstractTestObject[Int] {
  type IntState[A] = State[Int, A]

  def apply[A](implicit rnd: DeterministicTestObject[A]): IntState[A] = rnd.generate

  implicit val testString: DeterministicTestObject[String] = new DeterministicTestObject[String] {
    def generate: IntState[String] = State(s => (s + 1, s.toString))
  }

  implicit val testInt: DeterministicTestObject[Int] = new DeterministicTestObject[Int] {
    def generate: IntState[Int] = State(s => (s + 1, s))
  }

  implicit val testLong: DeterministicTestObject[Long] = new DeterministicTestObject[Long] {
    def generate: IntState[Long] = State(s => (s + 1, s))
  }

  implicit val testDouble: DeterministicTestObject[Double] = new DeterministicTestObject[Double] {
    def generate: IntState[Double] = State(s => (s + 1, 0.1 * s))
  }

  implicit val testBoolean: DeterministicTestObject[Boolean] = new DeterministicTestObject[Boolean] {
    def generate: IntState[Boolean] = State(s => (s + 1, s % 2 == 0))
  }

  implicit val testDateTime: DeterministicTestObject[DateTime] = new DeterministicTestObject[DateTime] {
    def generate: IntState[DateTime] = State(s => (s + 1, new DateTime(2018, 3, 13, 0, 0).plusDays(s)))
  }

  implicit def testOption[A: DeterministicTestObject]: DeterministicTestObject[Option[A]] = new DeterministicTestObject[Option[A]] {
    def generate: IntState[Option[A]] =
      for {
        a <- implicitly[DeterministicTestObject[A]].generate
        s <- State.get
      } yield
        if (s % 2 == 0)
          Some(a)
        else
          None
  }

  implicit def testSeq[A: DeterministicTestObject]: DeterministicTestObject[Seq[A]] = new DeterministicTestObject[Seq[A]] {
    override def generate: IntState[Seq[A]] =
      for {
        one <- implicitly[DeterministicTestObject[A]].generate
        two <- implicitly[DeterministicTestObject[A]].generate
        three <- implicitly[DeterministicTestObject[A]].generate
      } yield Seq(one, two, three)
  }

  implicit def testSet[A: DeterministicTestObject]: DeterministicTestObject[Set[A]] = new DeterministicTestObject[Set[A]] {
    override def generate: IntState[Set[A]] =
      for {
        one <- implicitly[DeterministicTestObject[A]].generate
        two <- implicitly[DeterministicTestObject[A]].generate
        three <- implicitly[DeterministicTestObject[A]].generate
      } yield Set(one, two, three)
  }

  implicit val testHNil: TestHList[HNil] = new TestHList[HNil] {
    def generate: IntState[HNil] = State(s => (s, HNil))
  }

  implicit def testHCons[H, T <: HList](
    implicit
    head: DeterministicTestObject[H],
    tail: TestHList[T]
  ): TestHList[H :: T] = new TestHList[H :: T] {
    def generate: IntState[H :: T] = {
      for {
        h <- head.generate
        t <- tail.generate
      } yield h :: t
    }
  }

  implicit val testCNil: TestCoproduct[CNil] = new TestCoproduct[CNil] {
    def generate: IntState[CNil] = throw new RuntimeException
  }

  implicit def testCCons[H, T <: Coproduct](
    implicit
    inl: DeterministicTestObject[H],
    inr: TestCoproduct[T]
  ): TestCoproduct[H :+: T] = new TestCoproduct[H :+: T] {
    def generate: IntState[H :+: T] = {
      def or(l: H, rOpt: Option[T], s0: Int, s1: Int, s2: Int): IntState[H :+: T] = rOpt match {
        case Some(r) =>
          if (s2 % 2 == 0)
            State(_ => (s1, Inl(l)))
          else
            State(_ => (s0 + s2 - s1, Inr(r)))
        case None =>
          State(_ => (s1, Inl(l)))
      }

      for {
        s0 <- State.get
        l <- inl.generate
        s1 <- State.get
        rOpt <- try {
          inr.generate.map(Some(_))
        } catch {
          case e: Throwable =>
            State((s: Int) => (s, None))
        }
        s2 <- State.get
        out <- or(l, rOpt, s0, s1, s2)
      } yield out
    }
  }

  implicit def testHList[A, L <: HList](
    implicit
    gen: Generic.Aux[A, L],
    testHList: Lazy[TestHList[L]]
  ): DeterministicTestObject[A] = new DeterministicTestObject[A] {
    def generate: IntState[A] =
      testHList.value.generate.map(gen.from)
  }

  implicit def testCoproduct[A, C <: Coproduct](
    implicit
    gen: Generic.Aux[A, C],
    testCoproduct: Lazy[TestCoproduct[C]]
  ): DeterministicTestObject[A] = new DeterministicTestObject[A] {
    def generate: IntState[A] =
      testCoproduct.value.generate.map(gen.from)
  }
}
