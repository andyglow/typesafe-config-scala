package com.github.andyglow.config.time

import java.{time => jt}

import com.github.andyglow.config.ConfType.flexible
import com.github.andyglow.config.{ConfNum, ConfStr, ConfType}
import com.typesafe.config.impl.Internals


private[config] trait JavaTimeConfTypes {

  implicit val jtDurationT: ConfType[jt.Duration] = flexible[jt.Duration] { path =>
    {
      case ConfNum(v)    => jt.Duration ofMillis v.longValue()
      case cv@ConfStr(v) => jt.Duration ofNanos Internals.parseDuration(v, cv.origin(), path)
    }
  }

  implicit val periodT: ConfType[jt.Period] = flexible[jt.Period] { path =>
    {
      case ConfNum(v) => jt.Period.ofDays(v.intValue())
      case cv @ ConfStr(v) => Internals.parsePeriod(v, cv.origin(), path)
    }
  }
}
