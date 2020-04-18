package com.github.andyglow.config.time

import java.time._
import java.{time => jt}

import com.github.andyglow.config.ConfType._
import com.github.andyglow.config.{ConfNum, ConfObj, ConfStr, ConfType}
import com.typesafe.config.ConfigException


private[config] trait LowPriorityJavaTimeConfTypes {
  import LowPriorityJavaTimeConfTypes._

  implicit def jLocalDateT(
    implicit f: jt.format.DateTimeFormatter = jt.format.DateTimeFormatter.ISO_LOCAL_DATE,
             c: Clock = Clock.systemDefaultZone): ConfType[jt.LocalDate] = {

    flexible2[jt.LocalDate] { implicit path => {
      case ConfNum(v) => jt.LocalDate.ofEpochDay(v.longValue())
      case ConfObj(Extract.Date((m, d, y))) => jt.LocalDate.of(y, m, d)
      case ConfStr("now") => jt.LocalDate.now(c)
      case cv@ConfStr(v) => try jt.LocalDate.parse(v, f) catch {
        case ex: jt.format.DateTimeParseException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong local date '$v'", ex)
      }
    }}
  }

  implicit def jLocalTimeT(
    implicit f: jt.format.DateTimeFormatter = jt.format.DateTimeFormatter.ISO_LOCAL_TIME,
             c: Clock = Clock.systemDefaultZone): ConfType[jt.LocalTime] = {

    flexible2[jt.LocalTime] { implicit path => {
      case cv@ConfNum(v) => try jt.LocalTime.ofSecondOfDay(v.longValue()) catch {
        case ex: jt.DateTimeException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong local time '$v'", ex)
      }
      case ConfObj(Extract.Time((h, m, s))) => jt.LocalTime.of(h, m, s getOrElse 0)
      case ConfStr("now") => jt.LocalTime.now(c)
      case cv@ConfStr(v) => try jt.LocalTime.parse(v, f) catch {
        case ex: jt.format.DateTimeParseException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong local time '$v' (fmt: $f)", ex)
      }
    }}
  }

  implicit def jLocalDateTimeT(
    implicit f: jt.format.DateTimeFormatter = jt.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME,
             c: Clock = Clock.systemDefaultZone): ConfType[jt.LocalDateTime] = {

    flexible2[jt.LocalDateTime] { implicit path => {
      case ConfNum(v) => jt.LocalDateTime.ofEpochSecond(v.longValue(), 0, ZoneOffset.UTC)
      case ConfObj(Extract.DateTime((m, d, y, h, min, _))) => jt.LocalDateTime.of(y, m, d, h, min)
      case ConfObj(Extract.Date((m, d, y))) => jt.LocalDateTime.of(y, m, d, 0, 0)
      case ConfStr("now") => jt.LocalDateTime.now(c)
      case cv@ConfStr(v) => try jt.LocalDateTime.parse(v, f) catch {
        case ex: jt.format.DateTimeParseException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong local date time '$v'", ex)
      }
    }}
  }

  implicit def jZonedDateTimeT(
    implicit f: jt.format.DateTimeFormatter = jt.format.DateTimeFormatter.ISO_ZONED_DATE_TIME,
             c: Clock = Clock.systemDefaultZone): ConfType[jt.ZonedDateTime] = {

    flexible2[jt.ZonedDateTime] { implicit path => {
      case ConfNum(v) => jt.ZonedDateTime.ofInstant(jt.Instant.ofEpochSecond(v.longValue()), ZoneId.systemDefault())
      case ConfObj(Extract.DateTime((m, d, y, h, min, Some(id)))) => jt.ZonedDateTime.of(y, m, d, h, min, 0, 0, id)
      case ConfStr("now") => jt.ZonedDateTime.now(c)
      case cv@ConfStr(v) => try jt.ZonedDateTime.parse(v, f) catch {
        case ex: jt.format.DateTimeParseException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong zoned date time '$v'", ex)
      }
    }}
  }

  implicit def jOffsetDateTimeT(
    implicit f: jt.format.DateTimeFormatter = jt.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME,
             c: Clock = Clock.systemDefaultZone): ConfType[jt.OffsetDateTime] = {

    flexible2[jt.OffsetDateTime] { implicit path => {
      case ConfNum(v) => jt.OffsetDateTime.ofInstant(jt.Instant.ofEpochSecond(v.longValue()), ZoneId.systemDefault())
      case ConfObj(Extract.DateTime((m, d, y, h, min, Some(id: ZoneOffset)))) => jt.OffsetDateTime.of(y, m, d, h, min, 0, 0, id)
      case ConfStr("now") => jt.OffsetDateTime.now(c)
      case cv@ConfStr(v) => try jt.OffsetDateTime.parse(v, f) catch {
        case ex: jt.format.DateTimeParseException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong zoned date time '$v'", ex)
      }
    }}
  }

  implicit def jInstant(
    implicit c: Clock = Clock.systemDefaultZone): ConfType[jt.Instant] = {

    flexible[jt.Instant] { path => {
      case ConfNum(v) => jt.Instant.ofEpochSecond(v.longValue())
      case ConfStr("now") => jt.Instant.now(c)
      case cv@ConfStr(v) => try jt.Instant.parse(v) catch {
        case ex: jt.format.DateTimeParseException =>
          throw new ConfigException.BadValue(cv.origin(), path, s"Wrong instant '$v'", ex)
      }
    }}
  }

  implicit def jtDayOfWeekT(
    implicit c: Clock = Clock.systemDefaultZone): ConfType[jt.DayOfWeek] = {

    flexible[jt.DayOfWeek] { path => {
      case cv@ConfNum(v)  => try jt.DayOfWeek.of(v.intValue()) catch {
        case ex: IllegalArgumentException =>
          throw new ConfigException.BadValue(cv.origin(), path, s"Wrong day of week '$v'", ex)
      }
      case ConfStr("now") => jt.DayOfWeek.from(jt.LocalDateTime.now(c))
      case cv@ConfStr(v)  =>
        val x = v.toUpperCase
        (daysFull.get(x) orElse daysShort.get(x)) getOrElse {
          throw new ConfigException.BadValue(cv.origin(), path, s"Wrong day of week '$v'")
        }
    }
    }
  }

  implicit def jtMonthT(
    implicit c: Clock = Clock.systemDefaultZone): ConfType[jt.Month] = {

    flexible[jt.Month] { path => {
      case cv@ConfNum(v)  => try jt.Month.of(v.intValue()) catch {
        case ex: IllegalArgumentException =>
          throw new ConfigException.BadValue(cv.origin(), path, s"Wrong day of week '$v'", ex)
      }
      case ConfStr("now") => jt.Month.from(jt.LocalDateTime.now(c))
      case cv@ConfStr(v)  =>
        val x = v.toUpperCase
        (monthsFull.get(x) orElse monthsShort.get(x)) getOrElse {
          throw new ConfigException.BadValue(cv.origin(), path, s"Wrong day of week '$v'")
        }
    }
    }
  }

  implicit def jtYearT(
    implicit c: Clock = Clock.systemDefaultZone): ConfType[jt.Year] = {

    flexible[jt.Year] { path => {
      case cv@ConfNum(v) => try jt.Year.of(v.intValue()) catch {
        case ex: IllegalArgumentException => throw new ConfigException.BadValue(cv.origin(), path, s"Wrong year '$v'", ex)
      }
      case ConfStr("now") => jt.Year.from(jt.Instant.now(c))
      case cv@ConfStr(v) => try jt.Year.parse(v) catch {
        case ex: IllegalArgumentException => throw new ConfigException.BadValue(cv.origin(), path, s"Wrong year '$v'", ex)
      }
    }}
  }
}

private[config] object LowPriorityJavaTimeConfTypes {
  val daysFull : Map[String, DayOfWeek] = jt.DayOfWeek.values.map { d => (d.name, d) }.toMap
  val daysShort: Map[String, DayOfWeek] = jt.DayOfWeek.values.map { d => (d.name.take(3), d) }.toMap
  val monthsFull: Map[String, Month]    = jt.Month.values.map { d => (d.name, d) }.toMap
  val monthsShort: Map[String, Month]   = jt.Month.values.map { d => (d.name.take(3), d) }.toMap
}