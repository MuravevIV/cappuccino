package com.ilyamur.cappuccino.sqltool.component

import java.sql.{Connection, ResultSet}

import com.ilyamur.cappuccino.sqltool.SqlTypes._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryResultTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQueryResult") {

    val connection = mock[Connection]

    it("gets default typed value") {

      val resultSet = mock[ResultSet]
      val sqlQueryResult = new SqlQueryResult(resultSet, connection)

      when(resultSet.next()).thenReturn(true, false)
      when(resultSet.getInt(0)).thenReturn(1)

      val number: Int = sqlQueryResult.asSingleTyped(intTyped)

      number should be(1)
    }

    it("gets named typed value") {

      val resultSet = mock[ResultSet]
      val sqlQueryResult = new SqlQueryResult(resultSet, connection)

      when(resultSet.next()).thenReturn(true, true, false)
      when(resultSet.getInt(0)).thenReturn(1, 2)

      val list: List[Int] = sqlQueryResult.asListOfTyped(intTyped)

      list should be(List(1, 2))
    }
  }
}




























