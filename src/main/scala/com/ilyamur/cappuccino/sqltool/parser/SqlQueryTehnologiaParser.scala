package com.ilyamur.cappuccino.sqltool.parser

import java.util.regex.Pattern

import scala.collection.mutable.ArrayBuffer

class SqlQueryTehnologiaParser extends SqlQueryParser {

  private val pattern = Pattern.compile("<<([^<>]+)>>")

  /*
       Pattern p = Pattern.compile("cat");
       Matcher m = p.matcher("one cat two cats in the yard");
       StringBuffer sb = new StringBuffer();
       while (m.find()) {
           m.appendReplacement(sb, "dog");
       }
       m.appendTail(sb);
       System.out.println(sb.toString());
   */

  override def parse(queryString: String): SqlQueryAst = {
    val arrayBuffer = new ArrayBuffer[SqlQueryToken]()
    val matcher = pattern.matcher(queryString)
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
