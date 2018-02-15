package com.ilyamur.cappuccino.sqltool.component

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool.SqlTypes._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQuery") {

    val queryString = "SELECT 1 FROM dual"
    val connection = mock[Connection]

    val sqlQuery = new SqlQuery(queryString, connection)

    it("creates SqlQueryResult by execute") {

      val sqlQueryResult: SqlQueryResult = sqlQuery.execute()

      val one: Int = sqlQueryResult.asSingleTyped(intTyped)
    }
  }
}




























