package com.typesafe.config.impl

import java.time.Period

import com.typesafe.config.{ConfigOrigin, ConfigValue, ConfigValueType}


object Internals {

  def normalize(v: ConfigValue, t: ConfigValueType): ConfigValue =
    DefaultTransformer.transform(v.asInstanceOf[AbstractConfigValue], t)

  def parseDuration(x: String, o: ConfigOrigin, p: String): Long = SimpleConfig.parseDuration(x, o, p)

  def parseBytes(x: String, o: ConfigOrigin, p: String): Long = SimpleConfig.parseBytes(x, o, p).longValue()

  def parsePeriod(x: String, o: ConfigOrigin, p: String): Period = SimpleConfig.parsePeriod(x, o, p)
}
