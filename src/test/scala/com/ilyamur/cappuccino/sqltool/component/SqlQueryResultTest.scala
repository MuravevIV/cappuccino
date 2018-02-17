package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSet
import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.SqlTypes._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryResultTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQueryResult") {

    val resultSet = mock[ResultSet]
    val dataSource = mock[DataSource]

    val sqlQueryResult = new SqlQueryResult(resultSet, dataSource)

    it("gets default typed value") {

      val text: String = sqlQueryResult.asSingleTyped(stringTyped)
    }

    it("gets named typed value") {

      val text: List[String] = sqlQueryResult.asListOfTyped(stringTyped)
    }
  }
}




























