package com.github.andyglow.config

import java.text.DateFormat
import java.util.Date

import com.typesafe.config.{Config, ConfigException, ConfigFactory}
import org.scalatest._
import org.scalatest.Matchers._


class JavaUtilDateExtSpec extends FunSuite {
  import JavaUtilDateExtSpec._

  test("Parse out java.util.Date with fmt1") {
    implicit val fmt = DateFormat.getDateInstance
    val date = config.get[Date]("good-date-fmt1")
    date shouldBe new Date(118, 2, 12)
  }

  test("Parse out java.util.Date with fmt2") {
    implicit val fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
    val date = config.get[Date]("good-date-fmt2")
    date shouldBe new Date(118, 1, 18, 1, 15, 0)
  }

  test("Throw an exception on illegal date record") {
    implicit val fmt = DateFormat.getDateTimeInstance
    a[ConfigException.BadValue] should be thrownBy { config.get[Date]("wrong-date") }
  }

  test("compile error if no DateFormat in implicit scope") {
    """config.get[Date]("good-date-fmt2")""" shouldNot compile
  }
}

object JavaUtilDateExtSpec {

  val config: Config = ConfigFactory parseString
    """good-date-fmt1 = "Mar 12, 2018"
      |good-date-fmt2 = "2/18/18 1:15 AM"
      |wrong-date = "a-b-c"
    """.stripMargin
}
