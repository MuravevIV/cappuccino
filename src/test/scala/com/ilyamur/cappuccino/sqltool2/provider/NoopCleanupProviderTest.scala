package com.ilyamur.cappuccino.sqltool2.provider

import java.sql.Connection

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class NoopCleanupProviderTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the NoopCleanupProvider") {

    val connection = mock[Connection]

    val providerFunction = () => {
      connection
    }

    it("returns the same resource on multiple apply") {

      val closingProvider = new NoopCleanupProvider(providerFunction)

      val connection1 = closingProvider()
      val connection2 = closingProvider()

      connection1 shouldEqual connection2
    }

    it("leaves the underlying resource open") {

      val NoopCleanupProvider = new NoopCleanupProvider(providerFunction)

      val connection = NoopCleanupProvider()

      NoopCleanupProvider.cleanup()

      verify(connection, never()).close()
    }
  }
}
