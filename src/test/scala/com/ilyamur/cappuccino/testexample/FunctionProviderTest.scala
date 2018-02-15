package com.ilyamur.cappuccino.testexample

import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSpec, Matchers}

class FunctionProviderTest extends FunSpec
  with Matchers
  with MockitoSugar
  with PropertyChecks {

  describe("The function provider") {

    val functionProvider = new FunctionProvider

    it("has a method which returns a function which adds a number to a predefined number") {
      val addingFunction = functionProvider.addingFunction(23)

      forAll { (n: Int) =>
        addingFunction(n) should be(23 + n)
      }
    }

    it("has a method which returns a function which multiplies a text n times") {
      val textNTimesFunction = functionProvider.textNTimesFunction

      textNTimesFunction(2, "text") should be(Seq("text", "text"))
    }
  }
}
