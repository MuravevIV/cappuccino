package com.ilyamur.cappuccino.sqltoolv3.sql

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.ilyamur.cappuccino.sqltool.parser.{SqlQueryAst, SqlQueryParser, SqlQueryTextToken}
import com.ilyamur.cappuccino.sqltoolv3.fpattern.FPattern
import javax.sql.DataSource
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class ESqlQueryTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("The ESqlQuery") {

    val fPattern = new FPattern()

    it("should execute query") {

      val queryString = "SELECT 'message'"

      val dataSource = mock[DataSource]
      val queryParameters = mock[ESqlQueryParameters]
      val queryParser = mock[SqlQueryParser]
      val queryResultFactory = mock[ESqlQueryResult.Factory]
      val updateResultFactory = mock[ESqlUpdateResult.Factory]
      val queryResultRowFactory = mock[ESqlQueryResultRow.Factory]

      val connection = mock[Connection]
      val preparedStatement = mock[PreparedStatement]
      val resultSet = mock[ResultSet]
      val row = mock[ESqlQueryResultRow]
      val expectedQueryResult = mock[ESqlQueryResult]

      when(queryParser.parse(queryString)).thenReturn(SqlQueryAst(List(SqlQueryTextToken("SELECT 'message'"))))
      when(dataSource.getConnection).thenReturn(connection)
      when(connection.prepareStatement("SELECT 'message'")).thenReturn(preparedStatement)
      when(preparedStatement.executeQuery()).thenReturn(resultSet)
      when(resultSet.next()).thenReturn(true, false)
      when(queryResultRowFactory.apply(resultSet)).thenReturn(row)
      when(queryResultFactory.apply(List(row))).thenReturn(expectedQueryResult)

      val query = new ESqlQuery(dataSource, queryString, queryParameters, queryParser,
        queryResultFactory, updateResultFactory, queryResultRowFactory, fPattern)

      val actualQueryResult = query.executeQuery()

      assert(actualQueryResult == expectedQueryResult)
    }
  }

}
