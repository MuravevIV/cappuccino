package com.ilyamur.cappuccino.sqltool.component

import java.sql.{Connection, PreparedStatement}

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQuery") {

    val queryString = "SELECT 1 FROM dual"
    val connection = mock[Connection]
    val preparedStatement = mock[PreparedStatement]

    val sqlQuery = new SqlQuery(queryString, connection)

    it("creates SqlQueryResult by execute") {

      when(connection.prepareStatement(queryString)).thenReturn(preparedStatement)

      val sqlQueryResult: SqlQueryResult = sqlQuery.execute()

      assert(sqlQueryResult != null)
    }
  }
}




























