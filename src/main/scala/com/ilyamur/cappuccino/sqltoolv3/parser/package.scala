package com.ilyamur.cappuccino.sqltoolv3

import com.ilyamur.cappuccino.sqltool.parser.{SqlQueryParser, SqlQueryTehnologiaParser}
import com.softwaremill.macwire.wire
import com.softwaremill.tagging._

package object parser {

  trait Cached

  /*
    class OriginSqlQueryParser(sqlParser: SqlQueryTehnologiaParser) {

      def parse(queryString: String) = {
        sqlParser.parse(queryString)
      }
    }

    class CachedSqlQueryParser(originSqlQueryParser: OriginSqlQueryParser) extends SqlQueryParser {

      private val cache: Cache[String, SqlQueryAst] = {
        // todo conf
        // todo JMX stats
        CacheBuilder.newBuilder()
          .maximumSize(1024)
          .expireAfterWrite(600, TimeUnit.SECONDS)
          .build()
      }

      override def parse(queryString: String): SqlQueryAst = {
        cache.get(queryString, { () =>
          originSqlQueryParser.parse(queryString)
        })
      }
    }*/

  trait ParserModule {

    // lazy val cachedQueryParser = wire[SqlQueryTehnologiaParser].taggedWith[Cached]
    // lazy val originSqlQueryParser: OriginSqlQueryParser = wire[OriginSqlQueryParser]
    lazy val sqlQueryTehnologiaParser = wire[SqlQueryTehnologiaParser]
  }

}
