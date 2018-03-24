package com.ilyamur.cappuccino

import java.sql.{PreparedStatement, ResultSet}

import com.ilyamur.cappuccino.sqltool.parser.{SqlQueryParamToken, SqlQueryParser, SqlQueryTehnologiaParser}
import com.softwaremill.macwire._
import javax.sql.DataSource

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe._

package object sqltoolv3 {

  class FPattern {

    def using[C <: AutoCloseable, R](c: C)(f: C => R): R = {
      try {
        f(c)
      } finally {
        c.close()
      }
    }
  }

  //

  object ESqlTool {

    case class Context(queryStringParser: SqlQueryParser)

    class Factory(executorFactory: ESqlExecutor.Factory) {

      def apply(ctx: ESqlTool.Context): ESqlTool = {
        new ESqlTool(ctx, executorFactory)
      }
    }

  }

  class ESqlTool(ctx: ESqlTool.Context, executorFactory: ESqlExecutor.Factory) {

    def onDataSource(dataSource: DataSource): ESqlExecutor = {
      executorFactory.apply(ctx, dataSource)
    }
  }

  //

  object ESqlExecutor {

    case class Context(toolCtx: ESqlTool.Context, dataSource: DataSource)

    class Factory(queryFactory: ESqlQuery.Factory) {

      def apply(ctx: ESqlTool.Context, dataSource: DataSource): ESqlExecutor = {
        new ESqlExecutor(Context(ctx, dataSource), queryFactory)
      }
    }

  }

  class ESqlExecutor(ctx: ESqlExecutor.Context, queryFactory: ESqlQuery.Factory) {

    def query(queryString: String): ESqlQuery = {
      queryFactory.apply(ctx, queryString, List.empty)
    }
  }

  //

  object ESqlQuery {

    case class Context(executorCtx: ESqlExecutor.Context,
                       queryString: String,
                       queryParameters: List[ESqlQueryParameter])

    class Factory(queryResultFactory: ESqlQueryResult.Factory,
                  updateResultFactory: ESqlUpdateResult.Factory,
                  queryResultRowFactory: ESqlQueryResultRow.Factory,
                  fPattern: FPattern) {

      def apply(executorCtx: ESqlExecutor.Context, queryString: String, queryParameters: List[ESqlQueryParameter]): ESqlQuery = {
        val queryCtx = ESqlQuery.Context(executorCtx, queryString, List.empty)
        apply(queryCtx)
      }

      def apply(queryCtx: ESqlQuery.Context): ESqlQuery = {
        new ESqlQuery(queryCtx, this, queryResultFactory, updateResultFactory, queryResultRowFactory, fPattern)
      }
    }

  }

  class ESqlQuery(ctx: ESqlQuery.Context,
                  queryFactory: ESqlQuery.Factory,
                  queryResultFactory: ESqlQueryResult.Factory,
                  updateResultFactory: ESqlUpdateResult.Factory,
                  queryResultRowFactory: ESqlQueryResultRow.Factory,
                  fPattern: FPattern) {

    import fPattern._

    def params(pair: (String, Any), pairs: (String, Any)*): ESqlQuery = {
      val newQueryParameters = (pair :: pairs.toList).map { case (key, value) =>
        ESqlQueryParameter(key, value)
      }
      val updQueryCtx = ctx.copy(queryParameters = ctx.queryParameters ::: newQueryParameters)
      queryFactory.apply(updQueryCtx)
    }

    def executeQuery(): ESqlQueryResult = {

      val queryAst = ctx.executorCtx.toolCtx.queryStringParser.parse(ctx.queryString)
      val paramTokens = queryAst.getParamTokens

      val rows = using(ctx.executorCtx.dataSource.getConnection) { connection =>
        using(connection.prepareStatement(queryAst.getNormalForm)) { preparedStatement =>
          setParameters(preparedStatement, paramTokens)
          using(preparedStatement.executeQuery()) { resultSet =>
            toRows(resultSet)
          }
        }
      }

      queryResultFactory.apply(ctx, rows)
    }

    private def toRows(resultSet: ResultSet): List[ESqlQueryResultRow] = {
      val result = ArrayBuffer.empty[ESqlQueryResultRow]
      while (resultSet.next()) {
        val row = queryResultRowFactory.apply(ctx, resultSet)
        result.append(row)
      }
      result.toList
    }

    def executeUpdate(): ESqlUpdateResult = {

      val queryAst = ctx.executorCtx.toolCtx.queryStringParser.parse(ctx.queryString)
      val paramTokens = queryAst.getParamTokens

      val rowCount = using(ctx.executorCtx.dataSource.getConnection) { connection =>
        using(connection.prepareStatement(queryAst.getNormalForm)) { preparedStatement =>
          setParameters(preparedStatement, paramTokens)
          preparedStatement.executeUpdate()
        }
      }

      updateResultFactory.apply(ctx, rowCount)
    }

    private def setParameters(preparedStatement: PreparedStatement, paramTokens: List[SqlQueryParamToken]) = {
      paramTokens.zipWithIndex.foreach { case (paramToken, index) =>
        ctx.queryParameters.find(p => p.key == paramToken.name) match {
          case Some(param) =>
            preparedStatement.setObject(index + 1, param.value)
          case None =>
            // todo
            throw new IllegalArgumentException()
        }
      }
    }
  }

  //

  case class ESqlQueryParameter(key: String, value: Any)

  //

  object ESqlQueryResult {

    case class Context(queryCtx: ESqlQuery.Context, rows: List[ESqlQueryResultRow])

    class Factory {

      def apply(queryCtx: ESqlQuery.Context, rows: List[ESqlQueryResultRow]): ESqlQueryResult = {
        new ESqlQueryResult(Context(queryCtx, rows))
      }
    }

  }

  class ESqlQueryResult(ctx: ESqlQueryResult.Context) {

    def asList[T: TypeTag]: List[T] = {
      ctx.rows.map(row => row.as[T])
    }
  }

  //

  object ESqlQueryResultRow {

    case class Context(queryCtx: ESqlQuery.Context, resultSet: ResultSet)

    class Factory {

      def apply(queryCtx: ESqlQuery.Context, resultSet: ResultSet): ESqlQueryResultRow = {
        new ESqlQueryResultRow(Context(queryCtx, resultSet))
      }
    }

  }

  class ESqlQueryResultRow(ctx: ESqlQueryResultRow.Context) {

    def as[T: TypeTag]: T = {
      /*      val ttag = typeTag[T]

            ctx.reflectMap.get(ttag) {
              case Some(reflect) =>
                reflect
              case _ =>
                val reflect: Reflect[T] = resultSetReflection.ofType[T].prepareConstructorOn(ctx.resultSet.get)
                ctx.reflectMap.put(ttag, reflect)
                reflect
            }

            val t: T = reflect.createOn(ctx.resultSet.get)

            t*/

      ???
    }
  }

  //

  object ESqlUpdateResult {

    case class Context(queryCtx: ESqlQuery.Context, rowCount: Int)

    class Factory {

      def apply(queryCtx: ESqlQuery.Context, rowCount: Int): ESqlUpdateResult = {
        new ESqlUpdateResult(Context(queryCtx, rowCount))
      }
    }

  }

  class ESqlUpdateResult(ctx: ESqlUpdateResult.Context) {

    def verifyUpdates(): Unit = {
      // todo
    }
  }

  //

  trait ESqlToolModule {

    lazy val fPattern = wire[FPattern]
    lazy val queryParser = wire[SqlQueryTehnologiaParser]

    lazy val toolFactory = wire[ESqlTool.Factory]
    lazy val executorFactory = wire[ESqlExecutor.Factory]
    lazy val queryFactory = wire[ESqlQuery.Factory]
    lazy val queryResultFactory = wire[ESqlQueryResult.Factory]
    lazy val queryResultRowFactory = wire[ESqlQueryResultRow.Factory]
    lazy val updateResultFactory = wire[ESqlUpdateResult.Factory]
  }

}
