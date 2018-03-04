package com.ilyamur.cappuccino.sqltool.typemapping

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class MultiFunctionTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the MultiFunction") {

    var multiFunction = new MultiFunction[String]()

    it("registers and applies functions correctly") {

      multiFunction = multiFunction
        .register((n: Int) => n.toString)
        .register((n: Long) => (n * 2).toString)

      multiFunction.mApply(1) should be("1")
      multiFunction.mApply(2L) should be("4")
    }
  }
}
