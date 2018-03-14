package testobject

import scalaz.State

trait TestObject[S, A] {
  def generate: State[S, A]
}
