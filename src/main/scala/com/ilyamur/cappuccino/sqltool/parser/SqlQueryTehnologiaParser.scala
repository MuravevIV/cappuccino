package com.ilyamur.cappuccino.sqltool.parser

import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

import com.google.common.cache.{Cache, CacheBuilder}

import scala.collection.mutable.ArrayBuffer

class SqlQueryTehnologiaParser extends SqlQueryParser {

  private val paramPattern = Pattern.compile("<<([^<>]+)>>")

  private val cache: Cache[String, SqlQueryAst] = {
    // todo conf
    // todo JMX stats
    (new CacheBuilder[String, SqlQueryAst]())
      .maximumSize(1024)
      .expireAfterWrite(600, TimeUnit.SECONDS)
      .build()
  }

  override def parse(queryString: String): SqlQueryAst = {
    cache.get(queryString, { () =>
      parseForCache(queryString)
    })
  }

  private def parseForCache(queryString: String): SqlQueryAst = {
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
