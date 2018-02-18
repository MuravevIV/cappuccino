package com.ilyamur.cappuccino.sqltool.parser

trait SqlQueryParser {

  def parse(queryString: String): SqlQueryAst
}
