package com.ilyamur.cappuccino.sqltool2.component

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryResult2Test extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQueryResult") {

    val queryResult = new SqlQueryResult2()

    it("is a sequence of SqlQueryRow2") {

      queryResult.isInstanceOf[Seq[SqlQueryRow2]] shouldEqual true
    }
  }
}





















