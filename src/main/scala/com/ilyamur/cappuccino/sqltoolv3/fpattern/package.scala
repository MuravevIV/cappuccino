package com.ilyamur.cappuccino.sqltoolv3

import com.softwaremill.macwire.wire

package object fpattern {

  class FPattern {

    def using[C <: AutoCloseable, R](c: C)(f: C => R): R = {
      try {
        f(c)
      } finally {
        c.close()
      }
    }
  }

  trait FPatternModule {

    lazy val fPattern = wire[FPattern]
  }

}
