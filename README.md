# Typesafe Config (little scala wrapper)
[![Build Status](https://travis-ci.org/andyglow/typesafe-config-scala.svg)](https://travis-ci.org/andyglow/typesafe-config-scala)

Little scala extension to Typesafe Config

## Usage

Import it
`import com.github.andyglow.config._`

And then you will be able to

```
val config: Config = ...

// optional value
val stringOptProp: Option[String] = config.opt[String]("some.config.string-key")
val intOptProp: Option[Int] = config.opt[Int]("some.config.int-key")
...

// default values
val stringProp: String = config.getOrElse("some.config.string-key", "default")
val intProp: Int = config.opt[Int]("some.config.int-key", 76)
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