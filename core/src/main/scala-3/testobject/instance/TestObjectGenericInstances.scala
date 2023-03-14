package testobject.instance

import scala.annotation.tailrec
import cats.data.State
import scala.compiletime.*
import scala.deriving.*
import testobject.TestObject
import cats.Applicative
import cats.syntax.all.*

trait TestObjectGenericInstances { self: TestObjectInstances =>
  inline implicit def derive[A]: TestObject[A] =
    summonFrom {
      case x: TestObject[A] =>
        x
      case _ =>
        create[A]
    }

  inline final def create[A]: TestObject[A] =
    summonFrom {
      case _: Mirror.ProductOf[A] =>
        deriveProduct[A]
      case _: Mirror.SumOf[A] =>
        deriveSum[A]
    }

  inline def deriveProduct[A](using a: Mirror.ProductOf[A]): TestObject[A] = {
    def p: TestObject[A] = {
      val xs = deriveRec[a.MirroredElemTypes]
      productImpl[A](xs, a)
    }
    p
  }

  final def productImpl[A](xs: List[TestObject[?]], a: Mirror.ProductOf[A]): TestObject[A] =
    new TestObject[A] {
      def generate: IntState[A] =
        for {
          values <- xs.traverse(_.generate.widen[Any])
        } yield a.fromProduct(new SeqProduct(values))
    }

  inline def deriveSum[A](using a: Mirror.SumOf[A]): TestObject[A] = {
    def s: TestObject[A] = {
      val values = deriveRec[a.MirroredElemTypes]
      sumImpl[A](values)
    }
    s
  }

  final def sumImpl[A](values: List[TestObject[?]]): TestObject[A] = {
    new TestObject[A] {
      def generate: IntState[A] =
        for {
          allResults <- values.traverse(_.generate.widen[Any])
          l = allResults.minBy(_.getClass.getName)
          rOpt = allResults.tail.headOption.flatMap(_ => allResults.maxByOption(_.getClass.getName))
          s <- State.get
        } yield rOpt match {
          case Some(r) =>
            if (s % 2 == 0)
              l.asInstanceOf[A]
            else
              r.asInstanceOf[A]
          case None =>
            l.asInstanceOf[A]
        }
    }
  }

  inline def deriveRec[T <: Tuple]: List[TestObject[?]] =
    inline erasedValue[T] match {
      case _: EmptyTuple =>
        Nil
      case _: (t *: ts) =>
        derive[t] :: deriveRec[ts]
    }
}
