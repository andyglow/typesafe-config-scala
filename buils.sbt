import xerial.sbt.Sonatype._

name := "typesafe-config-scala"

organization := "com.github.andyglow"

homepage := Some(new URL("http://github.com/andyglow/typesafe-config-scala"))

startYear := Some(2017)

organizationName := "andyglow"

publishTo := sonatypePublishTo.value

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.12.8", "2.11.12")

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-deprecation",
  //  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture")

scalacOptions in (Compile, doc) ++= Seq(
  "-groups",
  "-implicits",
  "-no-link-warnings")

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
  "com.typesafe" % "config" % "1.3.3",
  "org.scalatest" %% "scalatest" % "3.0.1" % Test)
