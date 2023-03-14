package testobject.instance

import cats.data.State
import testobject.TestObject
import shapeless.:+:
import shapeless.::
import shapeless.CNil
import shapeless.Coproduct
import shapeless.Generic
import shapeless.HList
import shapeless.HNil
import shapeless.Inl
import shapeless.Inr
import shapeless.Lazy

trait TestObjectGenericInstances { self: TestObjectInstances =>
  implicit val testHNil: TestObject[HNil] = new TestObject[HNil] {
    def generate: IntState[HNil] = State(s => (s, HNil))
  }

  implicit def testHCons[H, T <: HList](implicit
    head: TestObject[H],
    tail: TestObject[T]
  ): TestObject[H :: T] = new TestObject[H :: T] {
    def generate: IntState[H :: T] = {
      for {
        h <- head.generate
        t <- tail.generate
      } yield h :: t
    }
  }

  implicit val testCNil: TestObject[CNil] = new TestObject[CNil] {
    def generate: IntState[CNil] = throw new RuntimeException
  }

  implicit def testCCons[H, T <: Coproduct](implicit
    inl: TestObject[H],
    inr: TestObject[T]
  ): TestObject[H :+: T] = new TestObject[H :+: T] {
    def generate: IntState[H :+: T] = {
      for {
        l <- inl.generate
        rOpt <-
          if (inr eq testCNil)
            State.pure[Int, Option[T]](None)
          else
            inr.generate.map(Some(_))
        s <- State.get
      } yield rOpt match {
        case Some(r) =>
          if (s % 2 == 0) Inl(l)
          else Inr(r)
        case None =>
          Inl(l)
      }
    }
  }

  implicit def testHList[A, L <: HList](implicit
    gen: Generic.Aux[A, L],
    testHList: Lazy[TestObject[L]]
  ): TestObject[A] = new TestObject[A] {
    def generate: IntState[A] =
      testHList.value.generate.map(gen.from)
  }

  implicit def testCoproduct[A, C <: Coproduct](implicit
    gen: Generic.Aux[A, C],
    testCoproduct: Lazy[TestObject[C]]
  ): TestObject[A] = new TestObject[A] {
    def generate: IntState[A] =
      testCoproduct.value.generate.map(gen.from)
  }
}
