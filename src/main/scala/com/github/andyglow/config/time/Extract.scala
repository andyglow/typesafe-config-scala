package com.github.andyglow.config.time

import java.time._

import com.github.andyglow.config.ConfObj
import com.github.andyglow.config.ConfType.ImplicitPath
import com.typesafe.config.{ConfigException, ConfigValue}

private[time] object Extract {

  object Date {

    def unapply(x: ConfigValue)(implicit path: ImplicitPath): Option[(Int, Int, Int)] = x match {
      case ConfObj(obj) =>
        val c = obj.toConfig
        def int(x: String): Option[Int] = if (c.hasPath(x)) Some(c.getInt(x)) else None
        for {
          y <- int("y") orElse int("year")
          m <- int("m") orElse int("month")
          d <- int("d") orElse int("day")
        } yield (m, d, y)
      case _ => None
    }
  }

  object Time {

    def unapply(x: ConfigValue)(implicit path: ImplicitPath): Option[(Int, Int, Option[Int])] = x match {
      case ConfObj(obj) =>
        val c = obj.toConfig
        def int(x: String): Option[Int] = if (c.hasPath(x)) Some(c.getInt(x)) else None
        for {
          h <- int("h") orElse int("hour")
          m <- int("m") orElse int("min") orElse int("minute")
          s  = int("s") orElse int("second")
        } yield (h, m, s)
      case _ => None
    }
  }

  object DateTime {

    def unapply(x: ConfigValue)(implicit path: ImplicitPath): Option[(Int, Int, Int, Int, Int, Option[ZoneId])] = x match {
      case ConfObj(obj) =>
        val c = obj.toConfig
        def int(x: String): Option[Int] = if (c.hasPath(x)) Some(c.getInt(x)) else None
        def str(x: String): Option[String] = if (c.hasPath(x)) Some(c.getString(x)) else None
        def zoneId(v: String, suffix: String): ZoneId = try ZoneId.of(v) catch {
          case ex: DateTimeException => throw new ConfigException.BadValue(obj.origin(), s"${path.v}.$suffix", s"Wrong zone id '$v'", ex)
          case ex: zone.ZoneRulesException => throw new ConfigException.BadValue(obj.origin(), s"${path.v}.$suffix", s"Wrong zone id '$v'", ex)
        }
        def zoneOffset(v: String, suffix: String): ZoneOffset = try ZoneOffset.of(v) catch {
          case ex: DateTimeException => throw new ConfigException.BadValue(obj.origin(), s"${path.v}.$suffix", s"Wrong zone offset '$v'", ex)
          case ex: zone.ZoneRulesException => throw new ConfigException.BadValue(obj.origin(), s"${path.v}.$suffix", s"Wrong zone offset '$v'", ex)
        }
        for {
          y     <- int("y") orElse int("year")
          m     <- int("m") orElse int("month")
          d     <- int("d") orElse int("day")
          h     <- int("h") orElse int("hour")
          min   <- int("min") orElse int("minute")
          id     = str("zone-id").map(zoneId(_, "zone-id")) orElse
                   str("zoneId").map(zoneId(_, "zoneId")) orElse
                   str("id").map(zoneId(_, "id"))
          offset = str("zone-offset").map(zoneOffset(_, "zone-offset")) orElse
                   str("zoneOffset").map(zoneOffset(_, "zoneOffset")) orElse
                   str("offset").map(zoneOffset(_, "offset"))
        } yield (m, d, y, h, min, offset orElse id)
      case _ => None
    }
  }
}
