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

    it("can not be applied twice") {

      val closingProvider = new ClosingProvider(providerFunction)

      closingProvider()

      an[IllegalStateException] should be thrownBy closingProvider()
    }

    it("closes the underlying resource") {

      val closingProvider = new ClosingProvider(providerFunction)

      val connection = closingProvider()

      closingProvider.cleanup()

      verify(connection).close()
    }
  }
}
