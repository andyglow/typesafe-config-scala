package com.github.andyglow.config

import java.{ util => ju, time => jt, text => jtxt }

import com.typesafe.config._
import com.typesafe.config.impl.Internals

import scala.concurrent.duration.{Duration, FiniteDuration}


trait ConfType[T] { self =>
  type Internal
  def make(x: ConfigValue, path: String): T
  def map[TT](f: T => TT): ConfType.Aux[TT, Internal] = new ConfType[TT] {
    type Internal = self.Internal
    def make(x: ConfigValue, path: String): TT = f(self.make(x, path))
  }
}

object ConfType {
  type Aux[T, I] = ConfType[T] { type Internal = I }

  def homogenous[T, I](original: ConfigValueType, f: I => T): ConfType.Aux[T, I] = new ConfType[T] {
    type Internal = I

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

  def flexible[T](pf: String => PartialFunction[ConfigValue, T]): ConfType[T] = new ConfType[T] {
    type Internal = Nothing

    def make(x: ConfigValue, path: String): T = pf(path)(x)
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
  implicit val jDurationT: ConfType[jt.Duration] = flexible[jt.Duration] { path =>
    {
      case ConfNum(v)    => jt.Duration ofMillis v.longValue()
      case cv@ConfStr(v) => jt.Duration ofNanos Internals.parseDuration(v, cv.origin(), path)
    }
  }
  implicit val finDurT: ConfType[FiniteDuration] = jDurationT map { jd => Duration.fromNanos(jd.toNanos).toCoarsest.asInstanceOf[FiniteDuration] }
  implicit val durationT: ConfType[Duration] = finDurT map { _.asInstanceOf[Duration] }
  implicit val sizeInBytesT: ConfType[SizeInBytes] = flexible[SizeInBytes] { path =>
    {
      case ConfNum(v) => SizeInBytes(v.longValue())
      case cv @ ConfStr(v) => SizeInBytes(Internals.parseBytes(v, cv.origin(), path))
    }
  }
  implicit val periodT: ConfType[jt.Period] = flexible[jt.Period] { path =>
    {
      case ConfNum(v) => jt.Period.ofDays(v.intValue())
      case cv @ ConfStr(v) => Internals.parsePeriod(v, cv.origin(), path)
    }
  }
  implicit val memorySizeT: ConfType[ConfigMemorySize] = flexible[ConfigMemorySize] { path =>
    {
      case ConfNum(v)    => ConfigMemorySize.ofBytes(v.longValue())
      case cv@ConfStr(v) => ConfigMemorySize.ofBytes(Internals.parseBytes(v, cv.origin(), path))
    }
  }
  implicit def juDateT(implicit f: jtxt.DateFormat): ConfType[ju.Date] = flexible[ju.Date] { path =>
    {
      case ConfNum(v)    => new ju.Date(v.longValue())
      case cv@ConfStr(v) => try f.parse(v) catch {
        case ex: jtxt.ParseException =>
          throw new ConfigException.BadValue(cv.origin(), path, s"Wrong date '$v'", ex)
      }
    }
  }
}