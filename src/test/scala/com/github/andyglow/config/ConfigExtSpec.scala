package com.github.andyglow.config

import com.typesafe.config._
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.OptionValues._

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class ConfigExtSpec extends WordSpec with Matchers {

  def config = ConfigFactory parseString
    """
      |string="foo"
      |boolean=true
      |int=5
      |double=5.7
      |long=9865764764534
      |bytes=128m
      |duration=7s
      |configList=[15, "bar"]
      |config {
      | bar="baz"
      |}
      |configObject.x=99
      |memorySize=2k
    """.stripMargin

  "Config extension" should {
    "work with strings" in {
      config.get[String]("string") shouldBe Some("foo")
      config.get[Option[String]]("absent") shouldBe None
      config.getOrElse("string", "orElse") shouldBe "foo"
      config.getOrElse("absent", "orElse") shouldBe "orElse"
    }
    "work with booleans" in {
      config.get[Option[Boolean]]("boolean") shouldBe Some(true)
      config.get[Option[Boolean]]("absent") shouldBe None
      config.getOrElse("boolean", false) shouldBe true
      config.getOrElse("absent", true) shouldBe true
      config.getOrElse("absent", false) shouldBe false
    }
    "work with ints" in {
      config.get[Option[Int]]("int") shouldBe Some(5)
      config.get[Option[Int]]("absent") shouldBe None
      config.getOrElse("int", 11) shouldBe 5
      config.getOrElse("absent", 11) shouldBe 11
    }
    "work with doubles" in {
      config.get[Option[Double]]("double") shouldBe Some(5.7)
      config.get[Option[Double]]("absent") shouldBe None
      config.getOrElse("double", 11.34) shouldBe 5.7
      config.getOrElse("absent", 11.34) shouldBe 11.34
    }
    "work with longs" in {
      config.get[Option[Long]]("long") shouldBe Some(9865764764534l)
      config.get[Option[Long]]("absent") shouldBe None
      config.getOrElse("long", 77l) shouldBe 9865764764534l
      config.getOrElse("absent", 77l) shouldBe 77
    }
    "work with bytes" in {
      config.get[Option[Bytes]]("bytes") shouldBe Some(Bytes(134217728))
      config.get[Option[Bytes]]("absent") shouldBe None
      config.getOrElse("bytes", Bytes(56)) shouldBe Bytes(134217728)
      config.getOrElse("absent", Bytes(56)) shouldBe Bytes(56)
    }
    "work with durations" in {
      config.get[Option[Duration]]("duration") shouldBe Some(7.seconds)
      config.get[Option[Duration]]("absent") shouldBe None
      config.getOrElse("duration", 15.minutes) shouldBe 7.seconds
      config.getOrElse("absent", 15.minutes) shouldBe 15.minutes
    }
    "work with config lists" in {
      config.get[Option[ConfigList]]("configList") shouldBe 'defined
      config.get[Option[ConfigList]]("configList").value.unwrapped().asScala shouldBe List(15, "bar")
      config.get[Option[ConfigList]]("absent") shouldBe None
    }
    "work with configs" in {
      config.get[Option[Config]]("config") shouldBe 'defined
      config.get[Option[Config]]("config").value.hasPath("bar") shouldBe true
      config.get[Option[Config]]("config").value.getString("bar") shouldBe "baz"
      config.get[Option[Config]]("absent") shouldBe None
    }
    "work with config objects" in {
      config.get[Option[ConfigObject]]("configObject") shouldBe 'defined
      config.get[Option[ConfigObject]]("configObject").value.unwrapped().asScala shouldBe Map("x" -> 99)
      config.get[Option[ConfigObject]]("absent") shouldBe None
    }
    "work with memory sizes" in {
      config.get[Option[ConfigMemorySize]]("memorySize") shouldBe Some(ConfigMemorySize.ofBytes(2048))
      config.get[Option[ConfigMemorySize]]("absent") shouldBe None
      config.getOrElse("memorySize", ConfigMemorySize.ofBytes(1024)) shouldBe ConfigMemorySize.ofBytes(2048)
      config.getOrElse("absent", ConfigMemorySize.ofBytes(1024)) shouldBe ConfigMemorySize.ofBytes(1024)
    }

  }

}
