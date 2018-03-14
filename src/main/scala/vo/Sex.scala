package vo

sealed abstract class Sex(val value: String)

case object Female extends Sex("female")

case object Male extends Sex("male")

case class Unknown(v: String) extends Sex(s"Unknown($v)")
