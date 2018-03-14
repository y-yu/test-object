package testobject.instance

import shapeless.{Coproduct, HList}
import scalaz.State

trait AbstractTestObject[S] {
  trait TestHList[L <: HList] {
    def generate: State[S, L]
  }

  trait TestCoproduct[C <: Coproduct] {
    def generate: State[S, C]
  }
}
