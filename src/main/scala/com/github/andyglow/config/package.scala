package com.github.andyglow

import com.typesafe.config._


package object config {

  implicit class ConfigValueOps(private val x: ConfigValue) extends AnyVal {

    def isBool: Boolean = x.valueType() == ConfigValueType.BOOLEAN

    def isNum: Boolean = x.valueType() == ConfigValueType.NUMBER

    def isStr: Boolean = x.valueType() == ConfigValueType.STRING

    def isObj: Boolean = x.valueType() == ConfigValueType.OBJECT

    def isList: Boolean = x.valueType() == ConfigValueType.LIST
  }

  object ConfBool {
    def unapply(x: ConfigValue): Option[Boolean] = if (x.isBool) Some(x.unwrapped().asInstanceOf[Boolean]) else None
  }

  object ConfStr {
    def unapply(x: ConfigValue): Option[String] = if (x.isStr) Some(x.unwrapped().asInstanceOf[String]) else None
  }

  object ConfNum {
    def unapply(x: ConfigValue): Option[Number] = if (x.isNum) Some(x.unwrapped().asInstanceOf[Number]) else None
  }

  object ConfObj {
    def unapply(x: ConfigValue): Option[ConfigObject] = if (x.isObj) Some(x.asInstanceOf[ConfigObject]) else None
  }

  object ConfList {
    def unapply(x: ConfigValue): Option[ConfigList] = if (x.isList) Some(x.asInstanceOf[ConfigList]) else None
  }

  implicit class ConfigOps(val config: Config) extends AnyVal {

    def get[T: FromConf](path: String): T = FromConf[T](config, path)

    def getOrElse[T: FromConf](path: String, defValue: => T): T = opt[T](path) getOrElse defValue

    def opt[T: FromConf](path: String): Option[T] = FromConf[Option[T]](config, path)
  }
}
