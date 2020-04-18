# Typesafe Config (little scala wrapper)
[![Build Status](https://travis-ci.org/andyglow/typesafe-config-scala.svg)](https://travis-ci.org/andyglow/typesafe-config-scala)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.andyglow/typesafe-config-scala_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.andyglow/typesafe-config-scala_2.12)

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
val val1: Option[String] = config.get[Option[String]]("path")
val val2: String         = config.getOrElse[String]("path", "default")
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
- `Duration` (java.time)
- `Duration` (scala)
- `FiniteDuration`
- `ConfigList`
- `Config`
- `ConfigObject`
- `ConfigMemorySize`
- `Date` (java.util)
- `Date` (java.sql)
- `Time` (java.sql)
- `Timestamp` (java.sql)
- `LocalDate` (java.time)
- `LocalTime` (java.time)
- `LocalDateTime` (java.time)
- `OffsetDateTime` (java.time)
- `ZonedDateTime` (java.time)
- `DayOfWeek` (java.time)
- `Month` (java.time)
- `Year` (java.time)
- `Option[T]` where `T` is one of the supported types
- scala collections. `Seq[T]`, etc. 
  Any sort of collection types which has corresponding `CanBuildFrom`

### Extending
Also this can be extended by providing implementations for `FromConf` and/or `ConfType` 
(used for collections and might be implicitly reused for `FromConf`)
For example take a look at `com.github.andyglow.config.ConfType#juDateT` implementation 
and spec at `com.github.andyglow.config.JavaUtilDateExtSpec`