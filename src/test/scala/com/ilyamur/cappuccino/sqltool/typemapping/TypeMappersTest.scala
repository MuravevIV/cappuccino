package com.ilyamur.cappuccino.sqltool.typemapping

import com.ilyamur.cappuccino.sqltool.reflection.Reflection
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class TypeMappersTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the TypeMappers") {

    val reflection = new Reflection()
    var typeMappers = new TypeMappers()

    it("registers and applies multi-functions correctly") {

      typeMappers = typeMappers
        .register((n: Int) => n.toString)
        .register((n: Long) => (n * 2).toString)
        .register((s: String) => s.length)
        .register((n: Long) => n.intValue)

      typeMappers.forOutputType[String].mApply(1) should be("1")
      typeMappers.forOutputType[String].mApply(2L) should be("4")
      typeMappers.forOutputType[Int].mApply("foo") should be(3)
      typeMappers.forOutputType[Int].mApply(5L) should be(5)
    }
  }
}
