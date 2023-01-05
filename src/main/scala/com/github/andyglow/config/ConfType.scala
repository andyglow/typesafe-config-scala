package com.github.andyglow.config

import com.github.andyglow.config.time.JavaTimeConfTypes
import com.typesafe.config._
import com.typesafe.config.impl.Internals

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.reflect.ClassTag


trait ConfType[T] { self =>
  type Internal
  def name: String
  def make(x: ConfigValue, path: String): T
  def map[TT](f: T => TT): ConfType.Aux[TT, Internal] = new ConfType[TT] {
    type Internal = self.Internal
    def make(x: ConfigValue, path: String): TT = f(self.make(x, path))
    def name: String = self.name
  }
  override final def toString: String = name
}

trait LowPriorityConfType
  extends time.LowPriorityJavaTimeConfTypes
  with time.LowPriorityJavaUtilConfTypes
  with time.LowPriorityJavaSqlConfTypes

object ConfType extends JavaTimeConfTypes with LowPriorityConfType {
  type Aux[T, I] = ConfType[T] { type Internal = I }

  private[config] case class ImplicitPath(v: String) extends AnyVal

  private[config] def homogenous[T, I](original: ConfigValueType, f: I => T)(implicit ct: ClassTag[T]): ConfType.Aux[T, I] = new ConfType[T] {
    type Internal = I
    def name: String = ct.runtimeClass.getName
    def make(x: ConfigValue, path: String): T = {
      val v = Internals.normalize(x, original)
      if (v.valueType() != original)
        throw new ConfigException.WrongType(
          x.origin(),
          path,
          "list of " + original.name(),
          "list of " + x.valueType().name())

      f(x.unwrapped().asInstanceOf[Internal])
    }
  }

  private[config] def flexible[T](pf: String => PartialFunction[ConfigValue, T])(implicit ct: ClassTag[T]): ConfType[T] = new ConfType[T] {
    type Internal = Nothing
    def name: String = ct.runtimeClass.getName
    def make(x: ConfigValue, path: String): T = {
      val f = pf(path)
      if (f isDefinedAt x) f(x) else
        throw new ConfigException.BadValue(x.origin(), path, "Can't recognize")
    }
  }

  private[config] def flexible2[T](pf: ImplicitPath => PartialFunction[ConfigValue, T])(implicit ct: ClassTag[T]): ConfType[T] = new ConfType[T] {
    type Internal = Nothing
    def name: String = ct.runtimeClass.getName
    def make(x: ConfigValue, path: String): T = {
      val f = pf(ImplicitPath(path))
      if (f isDefinedAt x) f(x) else
        throw new ConfigException.BadValue(x.origin(), path, "Can't recognize")
    }
  }

  implicit val stringT: ConfType.Aux[String, String] = homogenous[String, String](ConfigValueType.STRING, identity)
  implicit val booleanT: ConfType.Aux[Boolean, Boolean] = homogenous[Boolean, Boolean](ConfigValueType.BOOLEAN, identity)
  implicit val shortT: ConfType.Aux[Short, Number] = homogenous[Short, Number](ConfigValueType.NUMBER, _.shortValue())
  implicit val intT: ConfType.Aux[Int, Number] = homogenous[Int, Number](ConfigValueType.NUMBER, _.intValue())
  implicit val longT: ConfType.Aux[Long, Number] = homogenous[Long, Number](ConfigValueType.NUMBER, _.longValue())
  implicit val doubleT: ConfType.Aux[Double, Number] = homogenous[Double, Number](ConfigValueType.NUMBER, _.doubleValue())
  implicit val floatT: ConfType.Aux[Float, Number] = homogenous[Float, Number](ConfigValueType.NUMBER, _.floatValue())
  implicit val configObjectT: ConfType.Aux[ConfigObject, ConfigObject] = homogenous[ConfigObject, ConfigObject](ConfigValueType.OBJECT, identity)
  implicit val configT: ConfType.Aux[Config, ConfigObject] = homogenous[Config, ConfigObject](ConfigValueType.OBJECT, _.toConfig)

  implicit val finDurT: ConfType[FiniteDuration] = jtDurationT map { jd => Duration.fromNanos(jd.toNanos).toCoarsest.asInstanceOf[FiniteDuration] }
  implicit val durationT: ConfType[Duration] = finDurT map { _.asInstanceOf[Duration] }
  implicit val sizeInBytesT: ConfType[SizeInBytes] = flexible[SizeInBytes] { path =>
    {
      case ConfNum(v) => SizeInBytes(v.longValue())
      case cv @ ConfStr(v) => SizeInBytes(Internals.parseBytes(v, cv.origin(), path))
    }
  }
  implicit val memorySizeT: ConfType[ConfigMemorySize] = flexible[ConfigMemorySize] { path =>
    {
      case ConfNum(v)    => ConfigMemorySize.ofBytes(v.longValue())
      case cv@ConfStr(v) => ConfigMemorySize.ofBytes(Internals.parseBytes(v, cv.origin(), path))
    }
  }
}