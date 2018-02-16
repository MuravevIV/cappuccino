package com.ilyamur.cappuccino.sqltool2.provider

import java.sql.Connection

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class ClosingProviderTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the ClosingProvider") {

    val connection = mock[Connection]

    val providerFunction = () => {
      connection
    }

    it("returns the same resource on multiple apply") {

      val closingProvider = new ClosingProvider(providerFunction)

      val connection1 = closingProvider()
      val connection2 = closingProvider()

      connection1 shouldEqual connection2
    }

    it("closes the underlying resource") {

      val closingProvider = new ClosingProvider(providerFunction)

      val connection = closingProvider()

      closingProvider.cleanup()

      verify(connection).close()
    }
  }
}
