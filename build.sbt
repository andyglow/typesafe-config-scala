import xerial.sbt.Sonatype._
import ReleaseTransformations._

name := "typesafe-config-scala"

organization := "com.github.andyglow"

homepage := Some(new URL("http://github.com/andyglow/typesafe-config-scala"))

startYear := Some(2017)

organizationName := "andyglow"

publishTo := sonatypePublishTo.value

scalaVersion := "2.13.1"

crossScalaVersions := Seq("2.13.1", "2.12.10", "2.11.12")

scalacOptions ++= {
  val options = Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    "-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-unused-import",
    "-Ywarn-numeric-widen",
    "-Xfuture",
    "-language:higherKinds")

  // WORKAROUND https://github.com/scala/scala/pull/5402
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 12)) => options.map {
      case "-Xlint"               => "-Xlint:-unused,_"
      case "-Ywarn-unused-import" => "-Ywarn-unused:imports,-patvars,-privates,-locals,-implicits"
      case other                  => other
    }
    case Some((2, n)) if n >= 13  => options.filterNot { opt =>
      opt == "-Yno-adapted-args" || opt == "-Xfuture" || opt == "-Xfatal-warnings" || opt == "-deprecation"
    }.map {
      case "-Ywarn-unused-import" => "-Ywarn-unused:imports,-patvars,-privates,-locals,-implicits"
      case other                  => other
    } :+ "-Xsource:2.13"
    case _             => options
  }
}

scalacOptions in (Compile, doc) ++= Seq(
  "-groups",
  "-implicits",
  "-no-link-warnings")


fork in Test := true

javaOptions in Test ++= Seq(
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
  "com.typesafe" % "config" % "1.4.0",
  "org.scalatest" %% "scalatest" % "3.2.1" % Test)

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