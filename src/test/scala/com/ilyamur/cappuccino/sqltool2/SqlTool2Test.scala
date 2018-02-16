package com.ilyamur.cappuccino.sqltool2

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool2.provider.CleanupProvider
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlTool2Test extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlTool2") {

    val tool: SqlTool2 = new SqlTool2()
    val connectionProvider: CleanupProvider[Connection] = mock[CleanupProvider[Connection]]
    val connection: Connection = mock[Connection]

    it("executes on connection provider") {

      when(connectionProvider.apply()).thenReturn(connection)

      val query = tool.on(connectionProvider)
        .query("SELECT 1 FROM dual")

      verify(connectionProvider, never()).apply()
      verify(connectionProvider, never()).cleanup()

      query.executeQuery()

      verify(connectionProvider).apply()
      verify(connectionProvider).cleanup()
    }
  }
}
