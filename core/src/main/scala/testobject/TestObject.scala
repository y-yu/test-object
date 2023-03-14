package testobject

import cats.data.State
import testobject.instance.TestObjectInstances

trait TestObject[A] {
  def generate: State[Int, A]

  final def eval: A = generate.run(0).value._2

  final def map[B](f: A => B): TestObject[B] =
    TestObject.testObjectCatsInstance.map(this)(f)

  final def flatMap[B](f: A => TestObject[B]): TestObject[B] =
    TestObject.testObjectCatsInstance.flatMap(this)(f)
}

object TestObject extends TestObjectInstances {
  def apply[A](implicit instance: TestObject[A]): TestObject[A] = instance

  def get[A](implicit instance: TestObject[A]): A = instance.eval
}
