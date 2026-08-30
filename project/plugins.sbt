// https://github.com/sbt/sbt/commit/505492ed332bebfc92508c98378ad968b0ac22ee
Compile / scalacOptions -= "-Xsource:3"

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.5.0")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.2")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")
