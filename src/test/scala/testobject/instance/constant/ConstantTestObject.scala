package testobject.instance.constant

import org.joda.time.DateTime
import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Lazy}
import testobject.TestObject
import testobject.instance.AbstractTestObject
import scalaz.State

trait ConstantTestObject[A] extends TestObject[Unit, A]

object ConstantTestObject extends AbstractTestObject[Unit] {
  type UnitState[A] = State[Unit, A]

  private def state[A](a: A): UnitState[A] =
    State(_ => ((), a))

  def apply[A](implicit rnd: ConstantTestObject[A]): A = rnd.generate(())._2

  implicit val testString: ConstantTestObject[String] = new ConstantTestObject[String] {
    def generate: UnitState[String] = state("string")
  }

  implicit val testInt: ConstantTestObject[Int] = new ConstantTestObject[Int] {
    def generate: UnitState[Int] = state(123)
  }

  implicit val testLong: ConstantTestObject[Long] = new ConstantTestObject[Long] {
    def generate: UnitState[Long] = state(1000L)
  }

  implicit val testDouble: ConstantTestObject[Double] = new ConstantTestObject[Double] {
    def generate: UnitState[Double] = state(0.5)
  }

  implicit val testBoolean: ConstantTestObject[Boolean] = new ConstantTestObject[Boolean] {
    def generate: UnitState[Boolean] = state(true)
  }

  implicit val testDateTime: ConstantTestObject[DateTime] = new ConstantTestObject[DateTime] {
    def generate: UnitState[DateTime] = state(new DateTime(2018, 3, 13, 0, 0))
  }

  implicit def testOption[A: ConstantTestObject]: ConstantTestObject[Option[A]] = new ConstantTestObject[Option[A]] {
    def generate: UnitState[Option[A]] = {
      state(Some(implicitly[ConstantTestObject[A]].generate(())._2))
    }
  }

  implicit def testSeq[A: ConstantTestObject]: ConstantTestObject[Seq[A]] = new ConstantTestObject[Seq[A]] {
    override def generate: UnitState[Seq[A]] =
      state((0 until 3).map { _ =>
        implicitly[ConstantTestObject[A]].generate(())._2
      })
  }

  implicit def testSet[A: ConstantTestObject]: ConstantTestObject[Set[A]] = new ConstantTestObject[Set[A]] {
    override def generate: UnitState[Set[A]] =
      state(Set(implicitly[ConstantTestObject[A]].generate(())._2))
  }

  implicit val testHNil: TestHList[HNil] = new TestHList[HNil] {
    def generate: UnitState[HNil] = state(HNil)
  }

  implicit def testHCons[H, T <: HList](
    implicit
    head: ConstantTestObject[H],
    tail: TestHList[T]
  ): TestHList[H :: T] = new TestHList[H :: T] {
    def generate: UnitState[H :: T] =
      for {
        h <- head.generate
        t <- tail.generate
      } yield h :: t
  }

  implicit val testCNil: TestCoproduct[CNil] = new TestCoproduct[CNil] {
    def generate: UnitState[CNil] = throw new RuntimeException()
  }

  implicit def testCCons[H, T <: Coproduct](
    implicit
    inl: ConstantTestObject[H],
    inr: TestCoproduct[T]
  ): TestCoproduct[H :+: T] = new TestCoproduct[H :+: T] {
    def generate: UnitState[H :+: T] = inl.generate.map(Inl(_))
  }

  implicit def testHList[A, L <: HList](
    implicit
    gen: Generic.Aux[A, L],
    testHList: Lazy[TestHList[L]]
  ): ConstantTestObject[A] = new ConstantTestObject[A] {
    def generate: UnitState[A] =
      testHList.value.generate.map(gen.from)
  }

  implicit def testCoproduct[A, C <: Coproduct](
    implicit
    gen: Generic.Aux[A, C],
    testCoproduct: Lazy[TestCoproduct[C]]
  ): ConstantTestObject[A] = new ConstantTestObject[A] {
    def generate: UnitState[A] =
      testCoproduct.value.generate.map(gen.from)
  }
}
