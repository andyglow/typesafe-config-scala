import xerial.sbt.Sonatype._
import ReleaseTransformations._
import ScalaVer._


name := "typesafe-config-scala"

organization := "com.github.andyglow"

homepage := Some(new URL("http://github.com/andyglow/typesafe-config-scala"))

startYear := Some(2017)

organizationName := "andyglow"

publishTo := sonatypePublishTo.value

scalaVersion := (ScalaVer.fromEnv getOrElse ScalaVer.default).full

crossScalaVersions := ScalaVer.values.map(_.full)

scalaV := ScalaVer.fromString(scalaVersion.value) getOrElse ScalaVer.default

scalacOptions := CompilerOptions(scalaV.value)

Compile / doc / scalacOptions ++= Seq(
  "-groups",
  "-implicits",
  "-no-link-warnings")

Test / fork := true

Test / javaOptions ++= Seq(
  "-Djava.locale.providers=COMPAT, CLDR")

licenses := Seq(("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")))

sonatypeProfileName := "com.github.andyglow"

publishMavenStyle := true

sonatypeProjectHosting := Some(
  GitHubHosting(
    "andyglow",
    "typesafe-config-scala",
    "andyglow@gmail.com"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/andyglow/typesafe-config-scala"),
    "scm:git@github.com:andyglow/typesafe-config-scala.git"))

developers := List(
  Developer(
    id    = "andyglow",
    name  = "Andriy Onyshchuk",
    email = "andyglow@gmail.com",
    url   = url("https://ua.linkedin.com/in/andyglow")))

releaseCrossBuild := true

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.6",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test)

releaseCrossBuild := true

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("+publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)