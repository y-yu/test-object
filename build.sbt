name := "test-object-example"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.scalaz" %% "scalaz-core" % "7.2.20",
  "joda-time" % "joda-time" % "2.9.9",
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "com.typesafe.play" %% "play-json-joda" % "2.6.7",
  "org.mockito" % "mockito-core" % "2.16.0" % "test",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
