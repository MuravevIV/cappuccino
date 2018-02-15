package com.ilyamur.cappuccino.sqltool.component

import java.sql.{Connection, ResultSet}

import com.ilyamur.cappuccino.sqltool.SqlTypes._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryResultTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQueryResult") {

    val resultSet = mock[ResultSet]
    val connection = mock[Connection]

    val sqlQueryResult = new SqlQueryResult(resultSet, connection)

    it("gets default typed value") {

      val one: Int = sqlQueryResult.asSingleTyped(intTyped)
    }

    it("gets named typed value") {

      val one: List[Int] = sqlQueryResult.asListOfTyped(intTyped)
    }
  }
}




























