package com.ilyamur.cappuccino.sqltool.parser

import org.apache.commons.lang3.StringUtils

case class SqlQueryAst(tokens: List[SqlQueryToken]) {

  val normalForm: String = {
    tokens
      .map {
        case SqlQueryTextToken(text) => text
        case _: SqlQueryParamToken => "?"
        case _ => StringUtils.EMPTY
      }
      .mkString
  }

  val paramTokens: List[SqlQueryParamToken] = {
    tokens.flatMap {
      case paramToken: SqlQueryParamToken => List(paramToken)
      case _ => List.empty
    }
  }
}
