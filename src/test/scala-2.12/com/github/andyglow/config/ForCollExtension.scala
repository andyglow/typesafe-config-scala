package com.github.andyglow.config

import com.github.andyglow.config.ConfigExtSpec.config
import org.scalatest._
import wordspec._
import matchers.should.Matchers._

import scala.collection.generic.CanBuildFrom
import scala.language.higherKinds
import scala.reflect.ClassTag


trait ForCollExtension { this: AnyWordSpec =>

  def forColl[C[_] <: TraversableOnce[_], T: ConfType](
    path: String,
    toList: C[T] => List[T],
    expect: T*)(implicit fc: FromConf[C[T]], cbf: CanBuildFrom[C[T], T, C[T]], ct: ClassTag[C[_]]): Unit = {

    val expectingItems = {
      val b = cbf()
      expect foreach { b += _ }
      b.result()
    }
    val coll = ct.runtimeClass.getSimpleName

    s"strict get for $coll" in {
      toList(config.get[C[T]](path)) shouldBe toList(expectingItems)
    }
  }
}
