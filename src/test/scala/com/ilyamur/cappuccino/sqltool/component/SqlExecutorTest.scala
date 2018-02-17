package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlExecutorTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlExecutor") {

    val dataSource = mock[DataSource]

    val sqlExecutor = new SqlExecutor(dataSource)

    it("creates SqlQuery by query") {

      val sqlQuery: SqlQuery = sqlExecutor.query("SELECT 1 FROM dual")

      sqlQuery
    }
  }
}




























