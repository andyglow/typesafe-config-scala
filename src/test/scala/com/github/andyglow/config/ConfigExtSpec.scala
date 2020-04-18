package com.github.andyglow.config

import java.time.{Period, Duration => JDuration}

import com.typesafe.config._
import org.scalatest.OptionValues._

import scala.collection.immutable.Queue
import scala.concurrent.duration._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class ConfigExtSpec extends AnyWordSpec with Matchers with ForCollExtension with AsScalaExtension {

  import ConfigExtSpec._

  "Scala Config extension" should {

    "handle collection of string" when multiValScope[String]("string-list", "a", "b")
    "handle collection of boolean" when multiValScope[Boolean]("boolean-list", true, true, false)
    "handle collection of int" when multiValScope[Int]("int-list", 1, 3, 5)
    "handle collection of double" when multiValScope[Double]("double-list", 1, 2, 3.87)
    "handle collection of long" when multiValScope[Long]("long-list", 89823475, 7, 2)
    "handle collection of bytes" when multiValScope[SizeInBytes]("bytes-list", SizeInBytes(128), SizeInBytes(16384))
    "handle collection of java duration" when multiValScope[JDuration]("duration-list", JDuration.ofSeconds(7), JDuration.ofMinutes(15))
    "handle collection of finite duration" when multiValScope[FiniteDuration]("duration-list", 7.seconds, 15.minutes)
    "handle collection of duration" when multiValScope[Duration]("duration-list", 7.seconds, 15.minutes)
    "handle collection of period" when multiValScope[Period]("period-list", Period.ofWeeks(2), Period.ofMonths(4))
    "handle collection of memory size" when multiValScope[ConfigMemorySize]("memorySize-list", ConfigMemorySize.ofBytes(2048), ConfigMemorySize.ofBytes(1024))

    "handle strings" when singleValScope[String]("string", "foo", "orElse")
    "handle booleans" when singleValScope[Boolean]("boolean", true, false)
    "handle ints" when singleValScope[Int]("int", 5, 11)
    "handle doubles" when singleValScope[Double]("double", 5.7, 11.34)
    "handle longs" when singleValScope[Double]("long", 9865764764534L, 77L)
    "handle bytes" when singleValScope[SizeInBytes]("bytes", SizeInBytes(134217728), SizeInBytes(56))
    "handle java durations" when singleValScope[JDuration]("duration", JDuration.ofSeconds(7), JDuration.ofMinutes(15))
    "handle durations" when singleValScope[Duration]("duration", 7.seconds, 15.minutes)
    "handle finite durations" when singleValScope[FiniteDuration]("duration", 7.seconds, 15.minutes)
    "handle period" when singleValScope[Period]("period", Period.ofWeeks(2), Period.of(1, 1, 1))
    "handle memory sizes" when singleValScope[ConfigMemorySize]("memorySize",
      ConfigMemorySize.ofBytes(2048),
      ConfigMemorySize.ofBytes(1024))

    "handle config lists" in {
      config.get[Option[ConfigList]]("configList") shouldBe Symbol("defined")
      config.get[Option[ConfigList]]("configList").value.unwrapped().asScala shouldBe List(15, "bar")
      config.get[Option[ConfigList]]("absent") shouldBe None
    }

    "handle configs" in {
      config.get[Option[Config]]("config") shouldBe Symbol("defined")
      config.get[Option[Config]]("config").value.hasPath("bar") shouldBe true
      config.get[Option[Config]]("config").value.getString("bar") shouldBe "baz"
      config.get[Option[Config]]("absent") shouldBe None
    }

    "handle config objects" in {
      config.get[Option[ConfigObject]]("configObject") shouldBe Symbol("defined")
      config.get[Option[ConfigObject]]("configObject").value.unwrapped().asScala shouldBe Map("x" -> 99)
      config.get[Option[ConfigObject]]("absent") shouldBe None
    }
  }

  def singleValScope[T: FromConf](path: String, expect: T, default: T): Unit = {

    "strict get" in {
      config.get[T](path) shouldBe expect
      a[ConfigException.Missing] should be thrownBy { config.get[T]("absent") }
    }

    "get with option" in {
      config.get[Option[T]](path) shouldBe Some(expect)
      config.get[Option[T]]("absent") shouldBe None
    }

    "opt" in {
      config.get[Option[T]](path) shouldBe Some(expect)
      config.get[Option[T]]("absent") shouldBe None
    }

    "getOrElse" in {
      config.getOrElse(path, default) shouldBe expect
      config.getOrElse("absent", default) shouldBe default
    }
  }

  def multiValScope[T: FromConf: ConfType](
    path: String,
    expect: T*): Unit = {

    forColl[Iterable, T](path, _.toList, expect: _*)
    forColl[Iterator, T](path, _.toList, expect: _*)
    forColl[Queue, T](path, _.toList, expect: _*)
    forColl[Seq, T](path, _.toList, expect: _*)
    forColl[IndexedSeq, T](path, _.toList, expect: _*)
    forColl[List, T](path, _.toList, expect: _*)
    forColl[Vector, T](path, _.toList, expect: _*)
    forColl[Set, T](path, _.toList, expect: _*)
  }
}

object ConfigExtSpec {

  val config: Config = ConfigFactory parseString
    """
      |string="foo"
      |boolean=true
      |int=5
      |double=5.7
      |long=9865764764534
      |bytes=128m
      |duration=7s
      |period=2w
      |configList=[15, "bar"]
      |config {
      | bar="baz"
      |}
      |configObject.x=99
      |memorySize=2k
      |string-list = ["a", "b"]
      |int-list = [1, 3, 5]
      |boolean-list = [true, true, false]
      |double-list = [1, 2, 3.87]
      |long-list = [89823475, 7, 2]
      |bytes-list = [128, "16k"]
      |duration-list = [7s, 15m]
      |period-list = [2w, 4m]
      |memorySize-list = [2k, 1k]
    """.stripMargin
}