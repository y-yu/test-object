name := "test-object-example"

version := "0.1"

scalaVersion := "2.13.10"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.10",
  "org.scalaz" %% "scalaz-core" % "7.3.7",
  "joda-time" % "joda-time" % "2.12.2",
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "com.typesafe.play" %% "play-json-joda" % "2.9.4",
  "org.mockito" % "mockito-core" % "5.2.0" % "test",
  "org.scalatest" %% "scalatest" % "3.2.15" % "test"
)
