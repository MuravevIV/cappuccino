package com.ilyamur.cappuccino.sqltool.parser

case class SqlQueryAst(tokens: List[SqlQueryToken]) {

  val normalForm: String = {
    tokens.map {
      case SqlQueryTextToken(text) => text
      case _: SqlQueryParamToken => "?"
    }.mkString
  }

  val paramTokens: List[SqlQueryParamToken] = {
    tokens.flatMap {
      case paramToken: SqlQueryParamToken => List(paramToken)
      case _ => List.empty
    }
  }
}
