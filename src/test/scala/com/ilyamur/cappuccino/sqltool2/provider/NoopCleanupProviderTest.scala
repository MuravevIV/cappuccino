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

    it("can not be applied twice") {

      val NoopCleanupProvider = new NoopCleanupProvider(providerFunction)

      NoopCleanupProvider()

      an[IllegalStateException] should be thrownBy NoopCleanupProvider()
    }

    it("leaves the underlying resource open") {

      val NoopCleanupProvider = new NoopCleanupProvider(providerFunction)

      val connection = NoopCleanupProvider()

      NoopCleanupProvider.cleanup()

      verify(connection, never()).close()
    }
  }
}
