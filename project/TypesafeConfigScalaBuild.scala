import sbt._
import sbt.Keys._
import bintray._
import BintrayKeys._
import scala.language.postfixOps

object TypesafeConfigScalaBuild extends Build {

  lazy val project = Project(
    "typesafe-config-scala",
    file("."),
    settings = BuildSettings.settings)

  object Bintray {

    lazy val settings = Seq(
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ => false },
      bintrayReleaseOnPublish in ThisBuild := false,
      licenses += ("GPL-3.0", url("https://www.gnu.org/licenses/gpl-3.0.html")),
      bintrayPackageLabels := Seq("scala", "tools", "typesafe", "config"),
      bintrayRepository := "scala-tools",
      homepage := Some(url("http://github.com/andyglow/typesafe-config-scala")),
      checksums := Seq(),
      pomExtra :=
        <scm>
          <url>git://github.com/andyglow/typesafe-config-scala.git</url>
          <connection>scm:git://github.com/andyglow/typesafe-config-scala.git</connection>
        </scm>
          <developers>
            <developer>
              <id>andyglow</id>
              <name>Andrey Onistchuk</name>
              <url>https://ua.linkedin.com/in/andyglow</url>
            </developer>
          </developers>
    )

  }

  object Dependencies {
    val scalatest = "org.scalatest" %% "scalatest" % "2.2.6"
    val typesafeConfig = "com.typesafe" % "config" % "1.3.0"
  }

  object BuildSettings {

    val ver = "0.1.1"

    lazy val settings = Defaults.coreDefaultSettings ++ Seq(
      version := ver,
      organization := "com.github.andyglow",

      scalaVersion := "2.11.8",

      scalacOptions in Compile ++= Seq("-unchecked", "-deprecation", "-target:jvm-1.8", "-Ywarn-unused-import"),
      scalacOptions in (Compile, doc) ++= Seq("-unchecked", "-deprecation", "-implicits", "-skip-packages", "samples"),
      scalacOptions in (Compile, doc) ++= Opts.doc.title("Typesafe Config Scala"),
      scalacOptions in (Compile, doc) ++= Opts.doc.version(ver),

      libraryDependencies ++= Seq(
        Dependencies.typesafeConfig,
        Dependencies.scalatest)

    ) ++ Bintray.settings
  }

}