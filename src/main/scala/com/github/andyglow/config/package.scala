package com.github.andyglow

import com.typesafe.config._

import scala.collection.JavaConverters._
import scala.concurrent.duration.{Duration, FiniteDuration}

package object config {

  type Getter[T] = (Config, String) => T

  case class Bytes(value: Long)

  implicit val stringGetter: Getter[String]                   = _ getString _
  implicit val booleanGetter: Getter[Boolean]                 = _ getBoolean _
  implicit val intGetter: Getter[Int]                         = _ getInt _
  implicit val doubleGetter: Getter[Double]                   = _ getDouble _
  implicit val longGetter: Getter[Long]                       = _ getLong _
  implicit val bytesGetter: Getter[Bytes]                     = (c, p) => Bytes(c getBytes p)
  implicit val durationGetter: Getter[Duration]               = (c, p) => Duration.fromNanos((c getDuration p).toNanos)
  implicit val finiteDurationGetter: Getter[FiniteDuration]   = (c, p) => Duration.fromNanos((c getDuration p).toNanos)
  implicit val configListGetter: Getter[ConfigList]           = _ getList _
  implicit val configGetter: Getter[Config]                   = _ getConfig _
  implicit val objectGetter: Getter[ConfigObject]             = _ getObject _
  implicit val memorySizeGetter: Getter[ConfigMemorySize]     = _ getMemorySize _
  implicit val configListStringGetter: Getter[List[String]]   = (config, path) => config.getStringList(path).asScala.toList
  implicit val configListBooleanGetter: Getter[List[Boolean]] = (config, path) => config.getBooleanList(path).asScala.toList.map(_.booleanValue())
  implicit val longListBooleanGetter: Getter[List[Long]]      = (config, path) => config.getLongList(path).asScala.toList.map(_.longValue())
  implicit val configListIntGetter: Getter[List[Int]]         = (config, path) => config.getIntList(path).asScala.toList.map(_.toInt)

  implicit def optionGetter[T](implicit getter: Getter[T]): Getter[Option[T]] = { (config, path) =>
    if (config hasPathOrNull path) {
      Some(getter(config, path))
    } else
      None
  }

  /**
    * enriches Typesafe configuration functionality with custom scala typesafe operations.
    */
  implicit class ConfigOps(val config: Config) extends AnyVal {

    def getOrElse[T: Getter](path: String, defValue: => T)(implicit getter: Getter[Option[T]]): T =
      getter(config, path) getOrElse defValue

    def get[T: Getter](path: String): T = {
      val getter = implicitly[Getter[T]]
      getter(config, path)
    }
  }
}
