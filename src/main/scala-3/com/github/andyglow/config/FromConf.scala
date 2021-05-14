package com.github.andyglow.config

import java.time.temporal.TemporalAmount
import java.time.{Period, Duration => JDuration}

import com.typesafe.config._

import scala.collection.BuildFrom
import scala.collection.immutable.Queue
import scala.concurrent.duration.{Duration, FiniteDuration}


trait FromConf[T] extends ((Config, String) => T)

object FromConf {

  def apply[T: FromConf](
    config: Config,
    path: String): T = {
    val fc = implicitly[FromConf[T]]
    fc(config, path)
  }

  implicit val stringFC: FromConf[String]               = _ getString _
  implicit val booleanFC: FromConf[Boolean]             = _ getBoolean _
  implicit val intFC: FromConf[Int]                     = _ getInt _
  implicit val doubleFC: FromConf[Double]               = _ getDouble _
  implicit val longFC: FromConf[Long]                   = _ getLong _
  implicit val finDurFC: FromConf[FiniteDuration]       = (c, p) => Duration.fromNanos((c getDuration p).toNanos).toCoarsest.asInstanceOf[FiniteDuration]
  implicit val durationFC: FromConf[Duration]           = (c, p) => Duration.fromNanos((c getDuration p).toNanos).toCoarsest
  implicit val jDurationFC: FromConf[JDuration]         = _ getDuration _
  implicit val periodFC: FromConf[Period]               = _ getPeriod _
  implicit val temporalFC: FromConf[TemporalAmount]     = _ getTemporal _
  implicit val configListFC: FromConf[ConfigList]       = _ getList _
  implicit val configFC: FromConf[Config]               = _ getConfig _
  implicit val objectFC: FromConf[ConfigObject]         = _ getObject _
  implicit val memorySizeFC: FromConf[ConfigMemorySize] = _ getMemorySize _
  implicit val sizeInBytesFC: FromConf[SizeInBytes]     = (c, p) => SizeInBytes(c getBytes p)

  implicit def optFC[T: FromConf]: FromConf[Option[T]] =
    (c, p) => if (c.hasPath(p)) Some(FromConf[T](c, p)) else None

  sealed trait Empty[C[_]] {
    def mk[T]: C[T]
  }

  object Empty {
    def mk[X, C[X]](f: => C[X]): Empty[C] = new Empty[C] {
      override def mk[T]: C[T] = f.asInstanceOf[C[T]]
    }

    implicit val emptyIterableOnce: Empty[IterableOnce] = mk(Nil)
    implicit val emptyIterator: Empty[Iterator] = mk(Iterator.empty)
    implicit val emptyIterable: Empty[Iterable] = mk(Nil)
    implicit val emptySeq: Empty[Seq] = mk(Nil)
    implicit val emptyList: Empty[List] = mk(Nil)
    implicit val emptyLazyList: Empty[LazyList] = mk(LazyList.empty)
    implicit val emptyQueue: Empty[Queue] = mk(Queue.empty)
    implicit val emptyIndexedSeq: Empty[IndexedSeq] = mk(IndexedSeq.empty)
    implicit val emptyVector: Empty[Vector] = mk(Vector.empty)
    implicit val emptySet: Empty[Set] = mk(Set.empty)
  }

  implicit def traversableFC[C[_], T](implicit
    ct: ConfType[T],
    cbf: BuildFrom[C[T], T, C[T]],
    e: Empty[C]): FromConf[C[T]] = { (c, p) =>
      import scala.jdk.CollectionConverters._

      val from = c.getList(p)
      val to = cbf.newBuilder(e.mk[T])
      to.sizeHint(from.size())
      from.asScala foreach { e =>
        to += ct.make(e, p)
      }

      to.result()
  }

  implicit def fromConfType[T](implicit ct: ConfType[T]): FromConf[T] = (c, p) => ct.make(c.getValue(p), p)
}
