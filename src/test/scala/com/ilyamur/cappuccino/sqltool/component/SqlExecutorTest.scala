package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.SqlTool
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlExecutorTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlExecutor") {

    val dataSource = mock[DataSource]

    val sqlExecutor = new SqlExecutor(dataSource, SqlTool.Context())

    it("creates SqlQuery by query") {

      val sqlQuery: SqlQuery = sqlExecutor.query("SELECT 1 FROM dual")
    }
  }
}




























