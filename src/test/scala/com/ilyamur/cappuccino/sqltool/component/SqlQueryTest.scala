package com.ilyamur.cappuccino.sqltool.component

import java.sql.{Connection, PreparedStatement, ResultSet}
import javax.sql.DataSource

import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQuery") {

    val queryString = "SELECT 'test_text' FROM dual"
    val dataSource = mock[DataSource]

    val sqlQuery = new SqlQuery(queryString, dataSource)

    it("executes plain query") {

      val connection = mock[Connection]
      val preparedStatement = mock[PreparedStatement]
      val resultSet = mock[ResultSet]

      when(dataSource.getConnection).thenReturn(connection)
      when(connection.prepareStatement(queryString)).thenReturn(preparedStatement)
      when(preparedStatement.executeQuery()).thenReturn(resultSet)

      val sqlQueryResult: SqlQueryResult = sqlQuery.executeQuery()

      verify(connection).close()
    }
  }
}




























