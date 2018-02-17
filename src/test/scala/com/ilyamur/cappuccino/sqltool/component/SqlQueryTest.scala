package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.SqlTypes._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQuery") {

    val queryString = "SELECT 'test_text' FROM dual"
    val dataSource = mock[DataSource]

    val sqlQuery = new SqlQuery(queryString, dataSource)

    it("creates SqlQueryResult by execute") {

      val sqlQueryResult: SqlQueryResult = sqlQuery.executeQuery()

      val text: String = sqlQueryResult.asSingleTyped(stringTyped)
    }
  }
}




























