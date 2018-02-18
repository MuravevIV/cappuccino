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
      queryAst.getNormalForm shouldEqual "SELECT 1 FROM dual"
      queryAst.getParamTokens shouldEqual List.empty
    }

    it("parses single-parameter query") {

      val queryAst = parser.parse("SELECT <<param>> FROM dual")

      queryAst.tokens shouldEqual List(
        SqlQueryTextToken("SELECT "),
        SqlQueryParamToken(name = "param"),
        SqlQueryTextToken(" FROM dual")
      )
      queryAst.getNormalForm shouldEqual "SELECT ? FROM dual"
      queryAst.getParamTokens shouldEqual List(SqlQueryParamToken(name = "param"))
    }

    it("parses multiple-parameter query") {

      val queryAst = parser.parse("SELECT <<param1>> FROM dual WHERE 1 = <<param2>>")

      queryAst.tokens shouldEqual List(
        SqlQueryTextToken("SELECT "),
        SqlQueryParamToken(name = "param1"),
        SqlQueryTextToken(" FROM dual WHERE 1 = "),
        SqlQueryParamToken(name = "param2")
      )
      queryAst.getNormalForm shouldEqual "SELECT ? FROM dual WHERE 1 = ?"
      queryAst.getParamTokens shouldEqual List(
        SqlQueryParamToken(name = "param1"),
        SqlQueryParamToken(name = "param2")
      )
    }
  }
}
