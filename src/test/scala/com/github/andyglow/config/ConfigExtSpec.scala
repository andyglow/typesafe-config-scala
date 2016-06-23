package com.github.andyglow.config

import com.typesafe.config._
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.OptionValues._
import com.github.andyglow.config._

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
      config.opt[String]("string") shouldBe Some("foo")
      config.opt[String]("absent") shouldBe None
      config.getOrElse("string", "orElse") shouldBe "foo"
      config.getOrElse("absent", "orElse") shouldBe "orElse"
    }
    "work with booleans" in {
      config.opt[Boolean]("boolean") shouldBe Some(true)
      config.opt[Boolean]("absent") shouldBe None
      config.getOrElse("boolean", false) shouldBe true
      config.getOrElse("absent", true) shouldBe true
      config.getOrElse("absent", false) shouldBe false
    }
    "work with ints" in {
      config.opt[Int]("int") shouldBe Some(5)
      config.opt[Int]("absent") shouldBe None
      config.getOrElse("int", 11) shouldBe 5
      config.getOrElse("absent", 11) shouldBe 11
    }
    "work with doubles" in {
      config.opt[Double]("double") shouldBe Some(5.7)
      config.opt[Double]("absent") shouldBe None
      config.getOrElse("double", 11.34) shouldBe 5.7
      config.getOrElse("absent", 11.34) shouldBe 11.34
    }
    "work with longs" in {
      config.opt[Long]("long") shouldBe Some(9865764764534l)
      config.opt[Long]("absent") shouldBe None
      config.getOrElse("long", 77l) shouldBe 9865764764534l
      config.getOrElse("absent", 77l) shouldBe 77
    }
    "work with bytes" in {
      config.opt[Bytes]("bytes") shouldBe Some(Bytes(134217728))
      config.opt[Bytes]("absent") shouldBe None
      config.getOrElse("bytes", Bytes(56)) shouldBe Bytes(134217728)
      config.getOrElse("absent", Bytes(56)) shouldBe Bytes(56)
    }
    "work with durations" in {
      config.opt[Duration]("duration") shouldBe Some(7.seconds)
      config.opt[Duration]("absent") shouldBe None
      config.getOrElse("duration", 15.minutes) shouldBe 7.seconds
      config.getOrElse("absent", 15.minutes) shouldBe 15.minutes
    }
    "work with config lists" in {
      config.opt[ConfigList]("configList") shouldBe 'defined
      config.opt[ConfigList]("configList").value.unwrapped().asScala shouldBe List(15, "bar")
      config.opt[ConfigList]("absent") shouldBe None
    }
    "work with configs" in {
      config.opt[Config]("config") shouldBe 'defined
      config.opt[Config]("config").value.hasPath("bar") shouldBe true
      config.opt[Config]("config").value.getString("bar") shouldBe "baz"
      config.opt[Config]("absent") shouldBe None
    }
    "work with config objects" in {
      config.opt[ConfigObject]("configObject") shouldBe 'defined
      config.opt[ConfigObject]("configObject").value.unwrapped().asScala shouldBe Map("x" -> 99)
      config.opt[ConfigObject]("absent") shouldBe None
    }
    "work with memory sizes" in {
      config.opt[ConfigMemorySize]("memorySize") shouldBe Some(ConfigMemorySize.ofBytes(2048))
      config.opt[ConfigMemorySize]("absent") shouldBe None
      config.getOrElse("memorySize", ConfigMemorySize.ofBytes(1024)) shouldBe ConfigMemorySize.ofBytes(2048)
      config.getOrElse("absent", ConfigMemorySize.ofBytes(1024)) shouldBe ConfigMemorySize.ofBytes(1024)
    }

  }

}
