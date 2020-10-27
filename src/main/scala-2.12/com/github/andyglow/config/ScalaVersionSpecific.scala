package com.github.andyglow.config

import scala.collection.JavaConverters._

private[config] object ScalaVersionSpecific {

  implicit class CollectionsToScala[T](private val coll: java.lang.Iterable[T]) extends AnyVal {

    def scala: Iterable[T] = coll.asScala
  }

  implicit class MapsToScala[K, V](private val coll: java.util.Map[K, V]) extends AnyVal {

    def scala: Map[K, V] = coll.asScala.toMap
  }
}
