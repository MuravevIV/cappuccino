package com.ilyamur.cappuccino.sqltool.component

import java.sql.Connection

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlExecutorTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlExecutor") {

    val connection = mock[Connection]

    val sqlExecutor = new SqlExecutor(connection)

    it("creates SqlQuery by query") {

      val sqlQuery: SqlQuery = sqlExecutor.query("SELECT 1 FROM dual")

      assert(sqlQuery != null)
    }
  }
}




























