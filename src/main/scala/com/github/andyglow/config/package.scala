package com.github.andyglow

import com.typesafe.config._
import scala.concurrent.duration.{FiniteDuration, Duration}

package object config {

  type Getter[T] = (Config, String) => T

  case class Bytes(value: Long)

  implicit val stringGetter: Getter[String]               = _ getString _
  implicit val booleanGetter: Getter[Boolean]             = _ getBoolean _
  implicit val intGetter: Getter[Int]                     = _ getInt _
  implicit val doubleGetter: Getter[Double]               = _ getDouble _
  implicit val longGetter: Getter[Long]                   = _ getLong _
  implicit val bytesGetter: Getter[Bytes]                 = (c, p) => Bytes(c getBytes p)
  implicit val durationGetter: Getter[Duration]           = (c, p) => Duration.fromNanos((c getDuration p).toNanos)
  implicit val finiteDurationGetter: Getter[FiniteDuration] = (c, p) => Duration.fromNanos((c getDuration p).toNanos)
  implicit val configListGetter: Getter[ConfigList]       = _ getList _
  implicit val configGetter: Getter[Config]               = _ getConfig _
  implicit val objectGetter: Getter[ConfigObject]         = _ getObject _
  implicit val memorySizeGetter: Getter[ConfigMemorySize] = _ getMemorySize _

  implicit class ConfigOps(val config: Config) extends AnyVal {
    def getOrElse[T : Getter](path: String, defValue: => T): T = opt[T](path) getOrElse defValue
    def opt[T : Getter](path: String): Option[T] = {
      if (config hasPathOrNull path) {
        val getter = implicitly[Getter[T]]
        Some(getter(config, path))
      } else
        None
    }
  }

}
