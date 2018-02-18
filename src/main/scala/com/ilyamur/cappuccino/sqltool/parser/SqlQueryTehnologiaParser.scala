package com.ilyamur.cappuccino.sqltool.parser

import java.util.regex.Pattern

import scala.collection.mutable.ArrayBuffer

class SqlQueryTehnologiaParser extends SqlQueryParser {

  private val paramPattern = Pattern.compile("<<([^<>]+)>>")

  override def parse(queryString: String): SqlQueryAst = {
    val arrayBuffer = new ArrayBuffer[SqlQueryToken]()
    val matcher = paramPattern.matcher(queryString)
    var start = 0
    var end = 0
    while (matcher.find()) {
      start = matcher.start()
      arrayBuffer.append(SqlQueryTextToken(queryString.substring(end, start)))
      arrayBuffer.append(SqlQueryParamToken(name = matcher.group(1)))
      end = matcher.end()
    }
    if (end < queryString.length) {
      arrayBuffer.append(SqlQueryTextToken(queryString.substring(end)))
    }
    SqlQueryAst(arrayBuffer.toList)
  }
}
