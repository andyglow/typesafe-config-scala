package com.github.andyglow.config

import com.github.andyglow.config.ConfigExtSpec.config
import com.github.andyglow.config.FromConf.Empty
import org.scalatest._
import org.scalatest.Matchers._

import scala.collection.BuildFrom
import scala.reflect.ClassTag

trait ForCollExtension { this: WordSpec =>

  def forColl[C[_], T: ConfType](
    path: String,
    toList: C[T] => List[T],
    expect: T*)(implicit fc: FromConf[C[T]], cbf: BuildFrom[C[T], T, C[T]], e: Empty[C], ct: ClassTag[C[_]]): Unit = {

    val expectingItems = {
      val b = cbf.newBuilder(e.mk[T])
      expect foreach { b += _ }
      b.result()
    }
    val coll = ct.runtimeClass.getSimpleName

    s"strict get for $coll" in {
      toList(config.get[C[T]](path)) shouldBe toList(expectingItems)
    }
  }
}
