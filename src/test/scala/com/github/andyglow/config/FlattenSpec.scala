package com.github.andyglow.config

import java.{ util => ju }
import com.typesafe.config.ConfigFactory
import org.scalatest.matchers.should.Matchers._
import org.scalatest.funsuite._
//import ScalaVersionSpecific._

class FlattenSpec extends AnyFunSuite {
  import FlattenSpec._

  test("apply[Map](conf)") {
    val props = Flatten[Map[String, String]](conf)
    props should contain only (
      "arr.0" -> "abc",
      "arr.1" -> "def",
      "bool" -> "true",
      "complex.arr.0.id" -> "1",
      "complex.arr.0.name" -> "foo",
      "complex.arr.1.id" -> "2",
      "complex.arr.1.name" -> "bar",
      "double" -> "123.67",
      "int" -> "20",
      "long" -> "987234985769873",
      "obj.f1" -> "f1",
      "obj.nested.arr.0" -> "0",
      "obj.nested.arr.1" -> "2",
      "obj.nested.f2" -> "f2",
      "str" -> "str")
  }

  test("apply(conf, init: Map)") {
    val props = Flatten(conf, Map("some.initial" -> "value", "bool" -> "false"))
    props should contain only (
      "some.initial" -> "value",
      "arr.0" -> "abc",
      "arr.1" -> "def",
      "bool" -> "true",
      "complex.arr.0.id" -> "1",
      "complex.arr.0.name" -> "foo",
      "complex.arr.1.id" -> "2",
      "complex.arr.1.name" -> "bar",
      "double" -> "123.67",
      "int" -> "20",
      "long" -> "987234985769873",
      "obj.f1" -> "f1",
      "obj.nested.arr.0" -> "0",
      "obj.nested.arr.1" -> "2",
      "obj.nested.f2" -> "f2",
      "str" -> "str")
  }


  test("apply[Properties](conf)") {
    val props = propsToMap { Flatten[ju.Properties](conf) }
    props should contain only (
      "arr.0" -> "abc",
      "arr.1" -> "def",
      "bool" -> "true",
      "complex.arr.0.id" -> "1",
      "complex.arr.0.name" -> "foo",
      "complex.arr.1.id" -> "2",
      "complex.arr.1.name" -> "bar",
      "double" -> "123.67",
      "int" -> "20",
      "long" -> "987234985769873",
      "obj.f1" -> "f1",
      "obj.nested.arr.0" -> "0",
      "obj.nested.arr.1" -> "2",
      "obj.nested.f2" -> "f2",
      "str" -> "str")
  }

  test("apply(conf, init: Properties)") {
    val init = new ju.Properties
    init.setProperty("some.initial", "value")
    init.setProperty("bool", "false")
    val props = propsToMap { Flatten(conf, init) }
    props should contain only (
      "some.initial" -> "value",
      "arr.0" -> "abc",
      "arr.1" -> "def",
      "bool" -> "true",
      "complex.arr.0.id" -> "1",
      "complex.arr.0.name" -> "foo",
      "complex.arr.1.id" -> "2",
      "complex.arr.1.name" -> "bar",
      "double" -> "123.67",
      "int" -> "20",
      "long" -> "987234985769873",
      "obj.f1" -> "f1",
      "obj.nested.arr.0" -> "0",
      "obj.nested.arr.1" -> "2",
      "obj.nested.f2" -> "f2",
      "str" -> "str")
  }

  test("apply(value)") {
    Flatten[Map[String, String]](conf.getValue("str")) should contain only ("root" -> "str")
    Flatten[Map[String, String]](conf.getValue("arr")) should contain only ("0" -> "abc", "1" -> "def")
  }
}

object FlattenSpec {

  val conf = ConfigFactory.parseString(
    """str = str
      |bool = true
      |int = 20
      |long = 987234985769873
      |double = 123.67
      |arr = [ abc, def ]
      |complex.arr = [
      |  { id: 1, name: foo },
      |  { id: 2, name: bar }]
      |obj {
      |  f1 = f1
      |  nested {
      |    f2 = f2
      |    arr = [ 0, 2 ]
      |  }
      |}
      |""".stripMargin)

  private def propsToMap(props: ju.Properties): Map[String, String] = {
    props.stringPropertyNames().toArray(Array.empty[String]).foldLeft[Map[String, String]](Map.empty) { case (acc, k) =>
      val v = props.getProperty(k)
      acc.updated(k, v)
    }
  }
}