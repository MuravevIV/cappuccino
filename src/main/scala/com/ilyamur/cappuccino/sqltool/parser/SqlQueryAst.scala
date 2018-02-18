package com.ilyamur.cappuccino.sqltool.parser

case class SqlQueryAst(tokens: List[SqlQueryToken]) {

  def getQueryStringNormalForm: String = {
    tokens.map {
      case SqlQueryTextToken(text) => text
      case SqlQueryParamToken(_, _) => "?"
    }.mkString
  }

  def getQueryParameters: List[SqlQueryParamToken] = {
    tokens.flatMap {
      case SqlQueryTextToken(_) => List.empty
      case paramToken: SqlQueryParamToken => List(paramToken)
    }
  }
}