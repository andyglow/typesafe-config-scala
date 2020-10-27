package com.github.andyglow.config

import java.text.NumberFormat
import java.{util => ju}

import com.typesafe.config._

import ScalaVersionSpecific._

/** Converts typesafe config into java Properties
  */
object Flatten {

  final case class Settings(numberFmt: NumberFormat)

  final object Settings {
    implicit val defaultSettings: Settings = {
      val fmt = NumberFormat.getInstance()
      fmt.setGroupingUsed(false)

      Settings(
        numberFmt = fmt)
    }
  }

  sealed trait Adapter[T] {
    def init: T
    def put(props: T, k: String, v: String): T
  }

  object Adapter {

    implicit object PropertiesAdapter extends Adapter[ju.Properties] {
      override def init: ju.Properties = new ju.Properties
      override def put(props: ju.Properties, k: String, v: String): ju.Properties = {
        props.setProperty(k, v)
        props
      }
    }

    implicit object StringMapAdapter extends Adapter[Map[String, String]] {
      override def init: Map[String, String] = Map.empty
      override def put(props: Map[String, String], k: String, v: String): Map[String, String] = props.updated(k, v)
    }
  }

  def apply[T](v: Config)(implicit adapter: Adapter[T], settings: Settings): T = apply(v, adapter.init)
  def apply[T](v: Config, init: T)(implicit adapter: Adapter[T], settings: Settings): T = move(v.root, true, Nil, init)

  def apply[T](v: ConfigValue)(implicit adapter: Adapter[T], settings: Settings): T = apply(v, adapter.init)
  def apply[T](v: ConfigValue, init: T)(implicit adapter: Adapter[T], settings: Settings): T = move(v, true, Nil, init)

  private def move[T](v: ConfigValue, isRoot: Boolean, path: List[String], props: T)(implicit adapter: Adapter[T], settings: Settings): T = {
    import settings._
    def k = if (isRoot) "root" else path mkString "."

    v match {
      case ConfStr(v)  => adapter.put(props, k, v)
      case ConfNum(v)  => adapter.put(props, k, numberFmt.format(v))
      case ConfBool(v) => adapter.put(props, k, v.toString)
      case ConfList(v) => v.scala.zipWithIndex.foldLeft(props) { case (props, (v, i)) =>  move(v, false, path :+ i.toString, props) }
      case ConfObj(v)  => v.scala.foldLeft(props) { case (props, (f, v)) =>  move(v, false, path :+ f, props) }
    }
  }

}