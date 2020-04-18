package com.github.andyglow.config.time

import java.text._
import java.time.Clock
import java.util._
import java.time._

import com.github.andyglow.config.ConfType.flexible2
import com.github.andyglow.config.{ConfNum, ConfObj, ConfStr, ConfType}
import com.typesafe.config.ConfigException


private[config] trait LowPriorityJavaUtilConfTypes {

  implicit def juDateT(
    implicit f: DateFormat,
             c: Clock = Clock.systemDefaultZone): ConfType[Date] = {

    flexible2[Date] { implicit path => {
      case ConfNum(v) => new Date(v.longValue())
      case ConfObj(Extract.DateTime((d, m, y, h, min, _))) => new GregorianCalendar(y + 1900, m, d, h, min).getTime
      case ConfObj(Extract.Date((m, d, y))) => new GregorianCalendar(y + 1900, m, d).getTime
      case ConfStr("now") => Date.from(Instant.now(c))
      case cv@ConfStr(v) => try f.parse(v) catch {
        case ex: ParseException =>
          throw new ConfigException.BadValue(cv.origin(), path.v, s"Wrong date '$v'. Format: ${f.toString}", ex)
      }
    }}
  }
}
