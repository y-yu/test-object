package testobject.instance

import org.scalatest.diagrams.Diagrams
import org.scalatest.flatspec.AnyFlatSpec
import testobject.TestObject

class TestObjectInstancesSpec extends AnyFlatSpec with Diagrams {
  sealed trait X {}
  case class X6(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int, f6: Int) extends X
  case class X5(f1: Int, f2: Int, f3: Int, f4: Int, f5: Int) extends X
  case class X4(f1: Int, f2: Int, f3: Int, f4: Int) extends X
  case class X3(f1: Int, f2: Int, f3: Int) extends X
  case class X2(f1: Int, f2: Int) extends X
  case class X1(f1: Int) extends X
  case object X0 extends X

  "TestObjectInstances" should "be the same between Scala 2 and 3" in {
    val actual = TestObject.get[(X, X, X, X, X, X, X, X, X, X, X, X, X, X, X, X)]
    actual.productIterator.asInstanceOf[Iterator[X]].zipWithIndex.foreach { case (value, index) =>
      if (index % 2 == 0)
        assert(value.isInstanceOf[X6])
      else
        assert(value === X0)
    }
  }
}
