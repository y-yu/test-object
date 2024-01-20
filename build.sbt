import ReleaseTransformations._
import sbt._
import Keys._
import org.scalafmt.sbt.ScalafmtPlugin.autoImport._

val projectName = "test-object"

val scala213 = "2.13.12"
val scala3 = "3.3.1"

val isScala3 = Def.setting(
  CrossVersion.partialVersion(scalaVersion.value).exists(_._1 == 3)
)

val baseSettings = Seq(
  organization := "com.github.y-yu",
  homepage := Some(url("https://github.com/y-yu")),
  licenses := Seq("MIT" -> url(s"https://github.com/y-yu/$projectName/blob/master/LICENSE")),
  scalaVersion := scala213,
  crossScalaVersions := Seq(scala213, scala3),
  scalacOptions ++= {
    if (isScala3.value) {
      Seq(
        "-Ykind-projector",
        "-source",
        "3.0-migration",
        "-Xmax-inlines",
        "100"
      )
    } else {
      Seq(
        "-release:11",
        "-Xlint:infer-any",
        "-Xsource:3",
        "-Ybackend-parallelism",
        "16"
      )
    }
  },
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-unchecked"
  ),
  scalafmtOnCompile := !isScala3.value
)

lazy val root =
  (project in file("."))
    .settings(baseSettings)
    .settings(
      name := projectName,
      publishArtifact := false,
      publish := {},
      publishLocal := {},
      publish / skip := true,
      addCommandAlias("SetScala3", s"++ $scala3!"),
      addCommandAlias("SetScala2", s"++ $scala213!")
    )
    .aggregate(core, example)

lazy val core =
  (project in file("core"))
    .settings(
      name := s"$projectName-core",
      description := "Dummy objects generator using datatype generic programming",
      libraryDependencies ++= {
        if (isScala3.value) {
          Nil
        } else {
          Seq(
            "com.chuusai" %% "shapeless" % "2.3.10",
            compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full)
          )
        }
      },
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core" % "2.10.0",
        "org.scalatest" %% "scalatest" % "3.2.17" % "test"
      )
    )
    .settings(baseSettings ++ publishSettings)

lazy val example =
  (project in file("example"))
    .settings(
      name := s"$projectName-example",
      libraryDependencies ++= {
        if (isScala3.value) {
          Seq("com.typesafe.play" %% "play-json" % "2.10.0-RC9")
        } else {
          Seq("com.typesafe.play" %% "play-json" % "2.10.3")
        }
      },
      libraryDependencies ++= Seq(
        "org.mockito" % "mockito-core" % "5.9.0" % "test",
        "org.scalatest" %% "scalatest" % "3.2.17" % "test"
      )
    )
    .settings(baseSettings)
    .dependsOn(core)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := Some(
    if (isSnapshot.value)
      Opts.resolver.sonatypeSnapshots
    else
      Opts.resolver.sonatypeStaging
  ),
  Test / publishArtifact := false,
  pomExtra :=
    <developers>
      <developer>
        <id>y-yu</id>
        <name>Yoshimura Hikaru</name>
        <url>https://github.com/y-yu</url>
      </developer>
    </developers>
      <scm>
        <url>git@github.com:y-yu/{projectName}.git</url>
        <connection>scm:git:git@github.com:y-yu/{projectName}.git</connection>
        <tag>{tagOrHash.value}</tag>
      </scm>,
  releaseTagName := tagName.value,
  releaseCrossBuild := true,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining("^ publishSigned"),
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeReleaseAll"),
    pushChanges
  )
)

val tagName = Def.setting {
  s"v${if (releaseUseGlobalVersion.value) (ThisBuild / version).value else version.value}"
}

val tagOrHash = Def.setting {
  if (isSnapshot.value) sys.process.Process("git rev-parse HEAD").lineStream_!.head
  else tagName.value
}
