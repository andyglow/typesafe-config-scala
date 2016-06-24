# Typesafe Config (little scala wrapper)
[![Build Status](https://travis-ci.org/andyglow/typesafe-config-scala.svg)](https://travis-ci.org/andyglow/typesafe-config-scala)
[![Download](https://api.bintray.com/packages/andyglow/scala-tools/typesafe-config-scala/images/download.svg) ](https://bintray.com/andyglow/scala-tools/typesafe-config-scala/_latestVersion)

Little scala extension to Typesafe Config

## Usage

### build.sbt
```
libraryDependencies += "com.github.andyglow" %% "typesafe-config-scala" % ${LATEST_VERSION} % Compile
```

### Code
Import it
```scala
import com.github.andyglow.config._
```

And then you will be able to

```scala
val config: Config = ...

// optional value
val stringOptProp: Option[String] = config.opt[String]("some.config.string-key")
val intOptProp: Option[Int] = config.opt[Int]("some.config.int-key")
...

// default values
val stringProp: String = config.getOrElse("some.config.string-key", "default")
val intProp: Int = config.getOrElse("some.config.int-key", 76)
```

### Supported types
- String
- Int
- Boolean
- Double
- Long
- Bytes (file size, etc...)
- Duration (`scala.concurrent.duration`)
- ConfigList
- Config
- ConfigObject
- ConfigMemorySize

### TODO
- Lists (with proper transformation to Scala)