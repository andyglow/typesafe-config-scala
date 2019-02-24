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
#### Get value by specifying needed type
```scala
val v1 = config.get[String]("path")
val v2 = config.get[Int]("path")
val v3 = config.get[FiniteDuration]("path")

```
#### Get rid of `if hasPath this else that` by leveraging notion of options
```scala
// optional value
val opt1: Option[String] = config.opt[String]("path")
val opt2: Option[String] = config.get[Option[String]]("path")
val val3: String         = config.getOrElse[String]("path", "default")
```

#### Have more flexible api for working with multi-values
```scala
val list1     = config.get[List[String]]("path")
val set2      = config.get[Set[Int]]("path")
val iterator3 = config.get[Iterator[ConfigMemorySize]]("path")
```

### Supported types
- `String`
- `Int`
- `Boolean`
- `Double`
- `Long`
- `Bytes` (file size, etc...)
- `Duration` (java)
- `Duration` (scala)
- `FiniteDuration`
- `ConfigList`
- `Config`
- `ConfigObject`
- `ConfigMemorySize`
- `Option[T]` where `T` is one of the supported types
- scala collections. `Seq[T]`, etc. Any sort of collection types which has corresponsing `CanBuildFrom`

### Extending
Also this can be extended by providing implementations for `FromConf` and/or `ConfType` 
(used for collections and might be implicitly reused for `FromConf`)
For example take a look at `com.github.andyglow.config.ConfType#juDateT` implementation 
and spec at `com.github.andyglow.config.JavaUtilDateExtSpec`

### TODO
- Experiment with macro-based config reader which could read out the config into a case class
- ADT for Enums (also macro)