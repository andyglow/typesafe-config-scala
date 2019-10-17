package com.github.andyglow.config

import scala.jdk.javaapi.{CollectionConverters => conv}

trait AsScalaExtension {

  implicit class JavaListOps[T](private val x: java.util.List[T]) {

    def asScala: List[T] = {

      conv.asScala(x).toList
    }
  }

  implicit class JavaMapOps[K, V](private val x: java.util.Map[K, V]) {

    def asScala: Map[K, V] = {

      conv.asScala(x).toMap
    }
  }
}
