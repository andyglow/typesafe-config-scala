package com.github.andyglow.config

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._

import java.sql._
import java.time.format.{DateTimeFormatter, FormatStyle}
import java.time.{Clock, Instant, LocalDate, ZoneOffset}


class JavaSqlDateExtSpec extends AnyFunSuite {
  import JavaSqlDateExtSpec._

  test("Parse out java.sql.Date from Num") {
    val date = config.get[Date]("good-date-num")
    date shouldBe new Date(1000L)
  }

  test("Parse out java.sql.Date from str: now") {
    implicit val clock = Clock.fixed(Instant.ofEpochMilli(90000000), ZoneOffset.UTC)
    val date = config.get[Date]("good-date-now")
    date shouldBe Date.valueOf(LocalDate.now(clock))
  }

  test("Parse out java.sql.Date from obj: short") {
    val date = config.get[Date]("good-date-obj1")
    date shouldBe Date.valueOf(LocalDate.of(1982, 11, 28))
  }

  test("Parse out java.sql.Date from obj: long") {
    val date = config.get[Date]("good-date-obj2")
    date shouldBe Date.valueOf(LocalDate.of(1982, 11, 28))
  }

  test("Parse out java.sql.Date with custom fmt") {
    implicit val fmt = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    val date = config.get[Date]("good-date-fmt2")
    date shouldBe Date.valueOf(LocalDate.of(2018, 2, 18))
  }

  // TODO:
  //  - time
  //  - timestamp
}

object JavaSqlDateExtSpec {

  val config: Config = ConfigFactory parseString
    """good-date-num = 1000
      |good-date-now = now
      |good-date-obj1 {
      | y = 1982
      | m = 11
      | d = 28
      |}
      |good-date-obj2 {
      | year = 1982
      | month = 11
      | day = 28
      |}
      |good-date-fmt2 = "2/18/18"
      |wrong-date = "a-b-c"
    """.stripMargin
}
