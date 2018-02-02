package com.ilyamur.cappuccino

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalacheck.Gen
import org.scalatest.mockito.MockitoSugar
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FunSpec, Matchers}

class FunctionUserTest extends FunSpec
  with Matchers
  with MockitoSugar
  with PropertyChecks {

  describe("The function user") {

    val functionProviderMock = mock[FunctionProvider]
    val textNTimesFunctionMock = mock[(Int, String) => Seq[String]]
    val functionUser = new FunctionUser(functionProviderMock)

    it("can do 1&1") {
      when(functionProviderMock.addingFunction(1)) thenReturn ((n: Int) => n + 1)

      functionUser.do1And1 should be(2)

      verify(functionProviderMock).addingFunction(1)
    }

    it("knows about batman") {
      when(functionProviderMock.textNTimesFunction) thenReturn textNTimesFunctionMock
      when(textNTimesFunctionMock.apply(8, "na")) thenReturn (1 to 8).map(_ => "na")

      functionUser.batman should be(Seq("na", "na", "na", "na", "na", "na", "na", "na", "batman"))

      verify(functionProviderMock).textNTimesFunction
      verify(textNTimesFunctionMock).apply(8, "na")
    }

    it("can calculate the answer (just with mocks)") {
      when(functionProviderMock.applies23ToFunction(any[Int => Int].apply)).thenReturn(42)

      functionUser.answer should be(42)

      val addToNineteenFunctionCaptor: ArgumentCaptor[Int => Int] = ArgumentCaptor.forClass(classOf[Int => Int])
      verify(functionProviderMock).applies23ToFunction(addToNineteenFunctionCaptor.capture())
      when(functionProviderMock.sumCurried(19)(1)).thenReturn(20)
      addToNineteenFunctionCaptor.getValue.apply(1) should be(20)
      verify(functionProviderMock).sumCurried(19)(1)
    }

    it("can calculate the answer (with a spy)") {
      val functionProvider = new FunctionProvider
      val functionProviderSpy = spy(functionProvider)

      val functionUser = new FunctionUser(functionProviderSpy)
      functionUser.answer should be(42)

      verify(functionProviderSpy).sumCurried(19)(23)
      val addToNineteenFunctionCaptor: ArgumentCaptor[Int => Int] = ArgumentCaptor.forClass(classOf[Int => Int])
      verify(functionProviderSpy).applies23ToFunction(addToNineteenFunctionCaptor.capture())
      forAll { (n: Int) =>
        addToNineteenFunctionCaptor.getValue.apply(n) should be(19 + n)
      }
    }

    it("likes developers") {
      when(functionProviderMock.textNTimesAsXCurried(any[Seq[String] => String].apply))
        .thenReturn {
          (n: Int) => (s: String) => (1 to 4).map(_ => "developers").mkString(", ")
        }

      functionUser.ballmer should be("developers, developers, developers, developers")

      val stringSeqToStringFunctionCaptor: ArgumentCaptor[Seq[String] => String] = ArgumentCaptor.forClass(classOf[Seq[String] => String])
      verify(functionProviderMock).textNTimesAsXCurried(stringSeqToStringFunctionCaptor.capture())
      val stringSeqToStringFunction = stringSeqToStringFunctionCaptor.getValue
      val stringListGenerator = Gen.listOf(Gen.alphaStr)
      forAll(stringListGenerator) { (stringLists: List[String]) ⇒
        stringSeqToStringFunction(stringLists) should be(stringLists.mkString(", "))
      }
    }
  }
}
























