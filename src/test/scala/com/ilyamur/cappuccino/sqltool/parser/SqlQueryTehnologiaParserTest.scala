package com.ilyamur.cappuccino.sqltool.parser

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryTehnologiaParserTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQueryTehnologiaParser") {

    val parser = new SqlQueryTehnologiaParser()

    it("parses non-parametrized query") {

      val queryAst = parser.parse("SELECT 1 FROM dual")

      queryAst.tokens shouldEqual List(SqlQueryTextToken("SELECT 1 FROM dual"))
    }

    it("parses single-parameter query") {

      val queryAst = parser.parse("SELECT <<param>> FROM dual")

      queryAst.tokens shouldEqual List(
        SqlQueryTextToken("SELECT "),
        SqlQueryParamToken(name = "param"),
        SqlQueryTextToken(" FROM dual")
      )
    }

    it("parses multiple-parameter query") {

      val queryAst = parser.parse("SELECT <<param1>> FROM dual WHERE 1 = <<param2>>")

      queryAst.tokens shouldEqual List(
        SqlQueryTextToken("SELECT "),
        SqlQueryParamToken(name = "param1"),
        SqlQueryTextToken(" FROM dual WHERE 1 = "),
        SqlQueryParamToken(name = "param2")
      )
    }
  }
}
