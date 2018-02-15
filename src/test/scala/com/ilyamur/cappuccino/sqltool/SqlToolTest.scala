package com.ilyamur.cappuccino.sqltool

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool.component.SqlExecutor
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlToolTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlTool") {

    val sqlTool = new SqlTool()

    val connection = mock[Connection]

    it("creates SqlExecutor on connection") {

      val sqlExecutor: SqlExecutor = sqlTool.on(connection)
    }
  }
}




























