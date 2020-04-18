package com.github.andyglow.config.time

import java.text._
import java.time._
import java.sql._
import java.time.format.DateTimeFormatter

import com.github.andyglow.config.ConfType.flexible2
import com.github.andyglow.config.{ConfNum, ConfObj, ConfStr, ConfType}
import com.typesafe.config.ConfigException


private[config] trait LowPriorityJavaSqlConfTypes {

  implicit def jsqlDateT(
    implicit f: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE,
             c: Clock = Clock.systemDefaultZone): ConfType[Date] = {

    flexible2[Date] { implicit path => {
      case ConfNum(v) => new Date(v.longValue())
      case ConfObj(Extract.Date((m, d, y))) => Date.valueOf(LocalDate.of(y, m, d))
      case ConfStr("now") => Date.valueOf(LocalDate.now(c))
      case cv@ConfStr(v) => try Date.valueOf(LocalDate.parse(v, f)) catch {
        case ex: ParseException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong sql.Date '$v'", ex)
      }
    }}
  }

  implicit def jsqlTimeT(
    implicit f: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME,
             c: Clock = Clock.systemDefaultZone): ConfType[Time] = {

    flexible2[Time] { implicit path => {
      case ConfNum(v) => new Time(v.longValue())
      case ConfObj(Extract.Time((h, m, s))) => Time.valueOf(LocalTime.of(h, m, s getOrElse 0))
      case ConfStr("now") => Time.valueOf(LocalTime.now(c))
      case cv@ConfStr(v) => try Time.valueOf(LocalTime.parse(v, f)) catch {
        case ex: ParseException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong sql.Time '$v'", ex)
      }
    }}
  }

  implicit def jsqlTimestampT(
    implicit f: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME,
             c: Clock = Clock.systemDefaultZone): ConfType[Timestamp] = {

    flexible2[Timestamp] { implicit path => {
      case ConfNum(v) => new Timestamp(v.longValue())
      case ConfObj(Extract.DateTime((m, d, y, h, min, _))) => Timestamp.valueOf(LocalDateTime.of(y, m, d, h, min, 0))
      case ConfStr("now") => Timestamp.valueOf(LocalDateTime.now(c))
      case cv@ConfStr(v) => try Timestamp.valueOf(LocalDateTime.parse(v, f)) catch {
        case ex: ParseException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong sql.Timestamp '$v'", ex)
      }
    }}
  }
}
