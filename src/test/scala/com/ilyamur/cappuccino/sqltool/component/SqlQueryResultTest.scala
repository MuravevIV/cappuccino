package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSet
import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.SqlTypes._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

import scala.collection.mutable.ArrayBuffer

class SqlQueryResultTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQueryResult") {

    val queryRows: ArrayBuffer[SqlQueryRow] = ArrayBuffer.empty
    val dataSource = mock[DataSource]

    val sqlQueryResult = new SqlQueryResult(queryRows, dataSource)

    it("gets single typed value") {

      val text: String = sqlQueryResult.asSingleTyped(stringTyped)
    }

    it("gets list of typed values") {

      val text: List[String] = sqlQueryResult.asListOfTyped(stringTyped)
    }
  }
}




























