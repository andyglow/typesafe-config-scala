package com.github.andyglow.config

import java.time._
import java.time.format.DateTimeFormatter

import com.typesafe.config.{Config, ConfigFactory}
import org.scalactic.source.Position
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers._


class JavaTimeDateExtSpec extends AnyFunSuite {
  import JavaTimeDateExtSpec._

  test("Parse java.time.LocalTime") {
    def checkFmt(k: String, fmt: DateTimeFormatter)(implicit pos: Position): Unit = {
      implicit def f: DateTimeFormatter = fmt
      val v = config.get[LocalTime](k)
      v shouldBe LocalTime.of(15, 1)
    }

    checkFmt("good-local-time-fmt1", DateTimeFormatter.ISO_LOCAL_TIME)
    checkFmt("good-local-time-fmt2", DateTimeFormatter.ofPattern("H:mm a"))
    checkFmt("good-local-time-fmt3", DateTimeFormatter.ofPattern("HH:m"))

    // struct
    config.get[LocalTime]("good-local-time-fmt4") shouldBe LocalTime.of(15, 1)

    // number
    config.get[LocalTime]("good-local-time-fmt5") shouldBe LocalTime.of(15, 1)

    // now
    implicit val clock = Clock.fixed(Instant.ofEpochSecond(1000), zoneId)
    config.get[LocalTime]("good-local-time-fmt6") shouldBe LocalTime.now(clock)
  }

  test("Parse java.time.LocalDate") {
    def checkFmt(k: String, fmt: DateTimeFormatter)(implicit pos: Position): Unit = {
      implicit def f: DateTimeFormatter = fmt
      val v = config.get[LocalDate](k)
      v shouldBe LocalDate.of(2011, 12, 3)
    }

    checkFmt("good-local-date-fmt1", DateTimeFormatter.ISO_LOCAL_DATE)
    checkFmt("good-local-date-fmt2", DateTimeFormatter.ofPattern("M/d/y"))
    checkFmt("good-local-date-fmt3", DateTimeFormatter.ofPattern("MMM d, yyyy"))

    // struct
    config.get[LocalDate]("good-local-date-fmt4") shouldBe LocalDate.of(2011, 12, 3)

    // number
    config.get[LocalDate]("good-local-date-fmt5") shouldBe LocalDate.of(2011, 12, 3)

    // now
    implicit val clock = Clock.fixed(Instant.ofEpochSecond(1000), zoneId)
    config.get[LocalDate]("good-local-date-fmt6") shouldBe LocalDate.now(clock)
  }

  test("Parse java.time.LocalDateTime") {
    def checkFmt(k: String, fmt: DateTimeFormatter)(implicit pos: Position): Unit = {
      implicit def f: DateTimeFormatter = fmt
      val v = config.get[LocalDateTime](k)
      v shouldBe LocalDateTime.of(2011, 12, 3, 1, 15)
    }

    checkFmt("good-local-date-time-fmt1", DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    checkFmt("good-local-date-time-fmt2", DateTimeFormatter.ofPattern("M/d/y h:m a"))
    checkFmt("good-local-date-time-fmt3", DateTimeFormatter.ofPattern("MMM d, yyyy h:m a"))

    // struct
    config.get[LocalDateTime]("good-local-date-time-fmt4") shouldBe LocalDateTime.of(2011, 12, 3, 1, 15)

    // number
    config.get[LocalDateTime]("good-local-date-time-fmt5") shouldBe LocalDateTime.of(2011, 12, 3, 1, 15)

    // now
    implicit val clock = Clock.fixed(Instant.ofEpochSecond(1000), zoneId)
    config.get[LocalDateTime]("good-local-date-time-fmt6") shouldBe LocalDateTime.now(clock)
  }

  test("Parse java.time.ZonedDateTime") {
    def checkFmt(k: String, fmt: DateTimeFormatter)(implicit pos: Position): Unit = {
      implicit def f: DateTimeFormatter = fmt
      val v = config.get[ZonedDateTime](k)
      v should (
        be(ZonedDateTime.of(2011, 12, 3, 1, 15, 0, 0, zoneId)) or
        be(ZonedDateTime.of(2011, 12, 3, 1, 15, 0, 0, ZoneId.of("+00:00")))
      )
    }

    checkFmt("good-zoned-date-time-fmt1", DateTimeFormatter.ISO_ZONED_DATE_TIME)
    checkFmt("good-zoned-date-time-fmt2", DateTimeFormatter.ofPattern("M/d/y h:m a z"))
    checkFmt("good-zoned-date-time-fmt3", DateTimeFormatter.ofPattern("MMM d, yyyy h:m a zzzz"))

    // struct
    config.get[ZonedDateTime]("good-zoned-date-time-fmt4") shouldBe ZonedDateTime.of(2011, 12, 3, 1, 15, 0, 0, zoneId)

    // number
    config.get[ZonedDateTime]("good-zoned-date-time-fmt5").toEpochSecond shouldBe zonedMillis

    // now
    implicit val clock = Clock.fixed(Instant.ofEpochSecond(1000), zoneId)
    config.get[ZonedDateTime]("good-zoned-date-time-fmt6") shouldBe ZonedDateTime.now(clock)
  }

  test("Parse java.time.OffsetDateTime") {
    def checkFmt(k: String, fmt: DateTimeFormatter)(implicit pos: Position): Unit = {
      implicit def f: DateTimeFormatter = fmt
      val v = config.get[OffsetDateTime](k)
      withClue(k) {
        v shouldBe OffsetDateTime.of(2011, 12, 3, 1, 15, 0, 0, ZoneOffset.of("-08:00"))
      }
    }

    checkFmt("good-offset-date-time-fmt1", DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    checkFmt("good-offset-date-time-fmt2", DateTimeFormatter.ofPattern("M/d/y h:m a XX"))
    checkFmt("good-offset-date-time-fmt3", DateTimeFormatter.ofPattern("MMM d, yyyy h:m a xxx"))

    // struct
    withClue("struct") {
      config.get[OffsetDateTime]("good-offset-date-time-fmt4") shouldBe OffsetDateTime.of(2011, 12, 3, 1, 15, 0, 0, ZoneOffset.of("-08:00"))
    }

    // number
    withClue("number") {
      config.get[OffsetDateTime]("good-offset-date-time-fmt5") shouldBe OffsetDateTime.of(2011, 12, 3, 1, 15, 0, 0, ZoneOffset.of("-08:00"))
    }

    // now
    withClue("now") {
      implicit val clock = Clock.fixed(Instant.ofEpochSecond(1000), zoneId)
      config.get[OffsetDateTime]("good-offset-date-time-fmt6") shouldBe OffsetDateTime.now(clock)
    }
  }

  test("Parse java.time.DayOfWeek") {
    def check(d: String, expectation: DayOfWeek)(implicit pos: Position): Unit = {
      for { i <- 1 to 3 } config.get[DayOfWeek](s"good-$d-fmt$i") shouldBe expectation
    }

    check("mon", DayOfWeek.MONDAY)
    check("tue", DayOfWeek.TUESDAY)
    check("wed", DayOfWeek.WEDNESDAY)
    check("thu", DayOfWeek.THURSDAY)
    check("fri", DayOfWeek.FRIDAY)
    check("sat", DayOfWeek.SATURDAY)
    check("sun", DayOfWeek.SUNDAY)

    // now
    implicit val clock = Clock.fixed(Instant.ofEpochSecond(1000), zoneId)
    config.get[DayOfWeek]("good-dow-fmt4") shouldBe DayOfWeek.from(LocalDateTime.now(clock))
  }

  test("Parse java.time.Month") {
    def check(m: String, expectation: Month)(implicit pos: Position): Unit = {
      for { i <- 1 to 3 } config.get[Month](s"good-$m-fmt$i") shouldBe expectation
    }

    check("jan", Month.JANUARY)
    check("feb", Month.FEBRUARY)
    check("mar", Month.MARCH)
    check("apr", Month.APRIL)
    check("may", Month.MAY)
    check("jun", Month.JUNE)
    check("jul", Month.JULY)
    check("aug", Month.AUGUST)
    check("sep", Month.SEPTEMBER)
    check("oct", Month.OCTOBER)
    check("nov", Month.NOVEMBER)
    check("dec", Month.DECEMBER)

    // now
    implicit val clock = Clock.fixed(Instant.ofEpochSecond(1000), zoneId)
    config.get[Month]("good-month-fmt4") shouldBe Month.from(LocalDateTime.now(clock))
  }

//  test("Throw an exception on illegal date record") {
//    implicit val fmt = DateFormat.getDateTimeInstance
//    a[ConfigException.BadValue] should be thrownBy { config.get[Date]("wrong-date") }
//  }
//

}

object JavaTimeDateExtSpec {

  val zoneId: ZoneId = ZoneId.of("UTC")

  val zonedMillis = ZonedDateTime.of(2011, 12, 3, 1, 15, 0, 0, zoneId).toEpochSecond

  val config: Config = ConfigFactory parseString
   s"""good-local-time-fmt1 = "15:01"
      |good-local-time-fmt2 = "15:01 PM"
      |good-local-time-fmt3 = "15:1"
      |good-local-time-fmt4.h = 15
      |good-local-time-fmt4.m = 1
      |good-local-time-fmt5 = 54060 // seconds of day
      |good-local-time-fmt6 = now
      |
      |good-local-date-fmt1 = "2011-12-03"
      |good-local-date-fmt2 = "12/3/2011"
      |good-local-date-fmt3 = "Dec 3, 2011"
      |good-local-date-fmt4.y = 2011
      |good-local-date-fmt4.m = 12
      |good-local-date-fmt4.d = 3
      |good-local-date-fmt5 = 15311 // epoch day
      |good-local-date-fmt6 = now
      |
      |good-local-date-time-fmt1 = "2011-12-03T01:15:00"
      |good-local-date-time-fmt2 = "12/3/2011 1:15 AM"
      |good-local-date-time-fmt3 = "Dec 3, 2011 1:15 AM"
      |good-local-date-time-fmt4.y = 2011
      |good-local-date-time-fmt4.m = 12
      |good-local-date-time-fmt4.d = 3
      |good-local-date-time-fmt4.h = 1
      |good-local-date-time-fmt4.min = 15
      |good-local-date-time-fmt5 = 1322874900 // epoch seconds
      |good-local-date-time-fmt6 = now
      |
      |good-zoned-date-time-fmt1 = "2011-12-03T01:15+00:00[UTC]"
      |good-zoned-date-time-fmt2 = "12/3/2011 1:15 AM +00:00"
      |good-zoned-date-time-fmt3 = "Dec 3, 2011 1:15 AM UTC"
      |good-zoned-date-time-fmt4.y = 2011
      |good-zoned-date-time-fmt4.m = 12
      |good-zoned-date-time-fmt4.d = 3
      |good-zoned-date-time-fmt4.h = 1
      |good-zoned-date-time-fmt4.min = 15
      |good-zoned-date-time-fmt4.id = UTC
      |good-zoned-date-time-fmt5 = $zonedMillis // epoch seconds
      |good-zoned-date-time-fmt6 = now
      |
      |good-offset-date-time-fmt1 = "2011-12-03T01:15-08:00"
      |good-offset-date-time-fmt2 = "12/3/2011 1:15 AM -0800"
      |good-offset-date-time-fmt3 = "Dec 3, 2011 1:15 AM -08:00"
      |good-offset-date-time-fmt4.y = 2011
      |good-offset-date-time-fmt4.m = 12
      |good-offset-date-time-fmt4.d = 3
      |good-offset-date-time-fmt4.h = 1
      |good-offset-date-time-fmt4.min = 15
      |good-offset-date-time-fmt4.offset = "-08:00"
      |good-offset-date-time-fmt5 = 1322903700 // epoch seconds
      |good-offset-date-time-fmt6 = now
      |
      |good-mon-fmt1 = Monday
      |good-mon-fmt2 = mon
      |good-mon-fmt3 = 1
      |good-tue-fmt1 = Tuesday
      |good-tue-fmt2 = tue
      |good-tue-fmt3 = 2
      |good-wed-fmt1 = Wednesday
      |good-wed-fmt2 = wed
      |good-wed-fmt3 = 3
      |good-thu-fmt1 = Thursday
      |good-thu-fmt2 = thu
      |good-thu-fmt3 = 4
      |good-fri-fmt1 = Friday
      |good-fri-fmt2 = fri
      |good-fri-fmt3 = 5
      |good-sat-fmt1 = Saturday
      |good-sat-fmt2 = sat
      |good-sat-fmt3 = 6
      |good-sun-fmt1 = Sunday
      |good-sun-fmt2 = sun
      |good-sun-fmt3 = 7
      |good-dow-fmt4 = now
      |
      |good-jan-fmt1 = January
      |good-jan-fmt2 = jan
      |good-jan-fmt3 = 1
      |good-feb-fmt1 = February
      |good-feb-fmt2 = feb
      |good-feb-fmt3 = 2
      |good-mar-fmt1 = March
      |good-mar-fmt2 = mar
      |good-mar-fmt3 = 3
      |good-apr-fmt1 = April
      |good-apr-fmt2 = apr
      |good-apr-fmt3 = 4
      |good-may-fmt1 = May
      |good-may-fmt2 = may
      |good-may-fmt3 = 5
      |good-jun-fmt1 = June
      |good-jun-fmt2 = jun
      |good-jun-fmt3 = 6
      |good-jul-fmt1 = July
      |good-jul-fmt2 = jul
      |good-jul-fmt3 = 7
      |good-aug-fmt1 = August
      |good-aug-fmt2 = aug
      |good-aug-fmt3 = 8
      |good-sep-fmt1 = September
      |good-sep-fmt2 = sep
      |good-sep-fmt3 = 9
      |good-oct-fmt1 = October
      |good-oct-fmt2 = oct
      |good-oct-fmt3 = 10
      |good-nov-fmt1 = November
      |good-nov-fmt2 = nov
      |good-nov-fmt3 = 11
      |good-dec-fmt1 = December
      |good-dec-fmt2 = dec
      |good-dec-fmt3 = 12
      |good-month-fmt4 = now
      |
      |wrong-date = "a-b-c"
    """.stripMargin
}


