package com.ilyamur.cappuccino.sqltool.reflection

import com.ilyamur.cappuccino.sqltool.Book
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class ReflectionTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the Reflection") {

    val reflection = new Reflection()

    it("gets ClassSymbol") {

      val classSymbol = reflection.getClassSymbol(classOf[Book])

      classSymbol.toString should be("class Book")
    }

    it("reflects case class with straight order of fields") {

      val ccr = reflection.forCaseClass[Book](List("id", "title", "year"))
      val book = ccr.createInstance(List(1L, "The catcher in the rye", 1951))

      book should be (Book(1L, "The catcher in the rye", 1951))
    }

    it("reflects case class with non-straight order of fields") {

      val ccr = reflection.forCaseClass[Book](List("title", "year", "id"))
      val book = ccr.createInstance(List("The catcher in the rye", 1951, 1L))

      book should be (Book(1L, "The catcher in the rye", 1951))
    }

    it("throws exception on case class construction with non-full argument list") {

      val thrown = the [IllegalArgumentException] thrownBy reflection.forCaseClass[Book](List("title", "year"))
      thrown.getMessage should startWith("Wrong reodering of fields")
    }

    it("throws exception on case class construction with extensive argument list") {

      val thrown = the [IllegalArgumentException] thrownBy reflection.forCaseClass[Book](List("title", "year", "id", "order"))
      thrown.getMessage should startWith("Wrong reodering of fields")
    }

    it("throws exception on case class construction with wrong argument list") {

      val thrown = the [IllegalArgumentException] thrownBy reflection.forCaseClass[Book](List("title", "year", "order"))
      thrown.getMessage should startWith("Wrong reodering of fields")
    }
  }
}
