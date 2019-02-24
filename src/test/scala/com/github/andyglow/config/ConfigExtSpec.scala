package com.github.andyglow.config

import java.time.{Period, Duration => JDuration}

import com.typesafe.config._
import org.scalatest.OptionValues._
import org.scalatest.{Matchers, WordSpec}

import scala.collection.JavaConverters._
import scala.collection.generic.CanBuildFrom
import scala.collection.immutable.Queue
import scala.concurrent.duration._
import scala.language.higherKinds
import scala.reflect.ClassTag


class ConfigExtSpec extends WordSpec with Matchers {

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
    "handle longs" when singleValScope[Double]("long", 9865764764534l, 77l)
    "handle bytes" when singleValScope[SizeInBytes]("bytes", SizeInBytes(134217728), SizeInBytes(56))
    "handle java durations" when singleValScope[JDuration]("duration", JDuration.ofSeconds(7), JDuration.ofMinutes(15))
    "handle durations" when singleValScope[Duration]("duration", 7.seconds, 15.minutes)
    "handle finite durations" when singleValScope[FiniteDuration]("duration", 7.seconds, 15.minutes)
    "handle period" when singleValScope[Period]("period", Period.ofWeeks(2), Period.of(1, 1, 1))
    "handle memory sizes" when singleValScope[ConfigMemorySize]("memorySize",
      ConfigMemorySize.ofBytes(2048),
      ConfigMemorySize.ofBytes(1024))

    "handle config lists" in {
      config.opt[ConfigList]("configList") shouldBe 'defined
      config.opt[ConfigList]("configList").value.unwrapped().asScala shouldBe List(15, "bar")
      config.opt[ConfigList]("absent") shouldBe None
    }

    "handle configs" in {
      config.opt[Config]("config") shouldBe 'defined
      config.opt[Config]("config").value.hasPath("bar") shouldBe true
      config.opt[Config]("config").value.getString("bar") shouldBe "baz"
      config.opt[Config]("absent") shouldBe None
    }

    "handle config objects" in {
      config.opt[ConfigObject]("configObject") shouldBe 'defined
      config.opt[ConfigObject]("configObject").value.unwrapped().asScala shouldBe Map("x" -> 99)
      config.opt[ConfigObject]("absent") shouldBe None
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
      config.opt[T](path) shouldBe Some(expect)
      config.opt[T]("absent") shouldBe None
    }

    "getOrElse" in {
      config.getOrElse(path, default) shouldBe expect
      config.getOrElse("absent", default) shouldBe default
    }
  }

  def multiValScope[T: FromConf: ConfType](
    path: String,
    expect: T*): Unit = {

    forColl[TraversableOnce, T](path, expect: _*)
    forColl[Traversable, T](path, expect: _*)
    forColl[Iterable, T](path, expect: _*)
    forColl[Iterator, T](path, expect: _*)
    forColl[Stream, T](path, expect: _*)
    forColl[Queue, T](path, expect: _*)
    forColl[Seq, T](path, expect: _*)
    forColl[IndexedSeq, T](path, expect: _*)
    forColl[List, T](path, expect: _*)
    forColl[Vector, T](path, expect: _*)
    forColl[Set, T](path, expect: _*)
  }

  def forColl[C[_] <: TraversableOnce[_], T: ConfType](
    path: String,
    expect: T*)(implicit fc: FromConf[C[T]], cbf: CanBuildFrom[C[T], T, C[T]], ct: ClassTag[C[_]]): Unit = {

    val expectingItems = {
      val b = cbf()
      expect foreach { b += _ }
      b.result().asInstanceOf[TraversableOnce[T]].toList
    }
    val coll = ct.runtimeClass.getSimpleName

    s"strict get for $coll" in {
      config.get[C[T]](path).toList shouldBe expectingItems
    }
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