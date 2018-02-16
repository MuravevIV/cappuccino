package com.ilyamur.cappuccino.sqltool2.component

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}
import com.ilyamur.cappuccino.sqltool.SqlTypes._

class SqlQueryRow2Test extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQueryRow") {

    val rowMetadata = mock[RowMetadata]
    val rowData = mock[RowData]

    val row = new SqlQueryRow2(rowMetadata, rowData)

    /*

    row-based:
     .asTyped(intTyped)
     .asTyped(intTyped, "one")
     .as[SomeObject]

     */

    it("get as Int") {

      val number: Int = row.asTyped(intTyped)
    }
  }
}





















