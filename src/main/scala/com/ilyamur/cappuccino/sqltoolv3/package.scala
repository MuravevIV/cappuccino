package com.ilyamur.cappuccino

import java.sql.{PreparedStatement, ResultSet}

import com.ilyamur.cappuccino.sqltool.parser.{SqlQueryParamToken, SqlQueryParser, SqlQueryTehnologiaParser}
import com.softwaremill.macwire._
import javax.sql.DataSource

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe._

package object sqltoolv3 {

  class Reflection {
  }

  //

  object ESqlContext {

    def empty: ESqlContext = ESqlContext()
  }

  case class ESqlContext(dataSource: Option[DataSource] = None,
                         queryString: Option[String] = None,
                         queryStringParser: Option[SqlQueryParser] = None,
                         resultSet: Option[ResultSet] = None,
                         queryRows: List[ESqlQueryResultRow] = List.empty,
                         updateRowCount: Option[Int] = None,
                         reflectMap: mutable.Map[TypeTag[_], Reflect[_]] = mutable.Map.empty)

  //

  class Reflect[T] {
  }

  //

  object ESqlTool {

    class Factory(executorFactory: ESqlExecutor.Factory) {

      def apply(ctx: ESqlContext = ESqlContext()): ESqlTool = {
        new ESqlTool(ctx, executorFactory)
      }
    }

  }

  class ESqlTool(ctx: ESqlContext, executorFactory: ESqlExecutor.Factory) {

    def onDataSource(dataSource: DataSource): ESqlExecutor = {
      val updCtx = ctx.copy(dataSource = Some(dataSource))
      executorFactory(updCtx)
    }
  }

  //

  object ESqlExecutor {

    class Factory(queryFactory: ESqlQuery.Factory) {

      def apply(ctx: ESqlContext): ESqlExecutor = {
        new ESqlExecutor(ctx, queryFactory)
      }
    }

  }

  class ESqlExecutor(ctx: ESqlContext, queryFactory: ESqlQuery.Factory) {

    def query(queryString: String): ESqlQuery = {
      val updCtx = ctx.copy(queryString = Some(queryString))
      queryFactory(updCtx)
    }
  }

  //

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

  object ESqlQuery {

    class Factory(queryResultFactory: ESqlQueryResult.Factory,
                  updateResultFactory: ESqlUpdateResult.Factory,
                  queryResultRowFactory: ESqlQueryResultRow.Factory,
                  fPattern: FPattern) {

      def apply(ctx: ESqlContext = ESqlContext.empty,
                queryParameters: List[ESqlQueryParameter] = List.empty): ESqlQuery = {
        new ESqlQuery(ctx, queryParameters, queryResultFactory, updateResultFactory, queryResultRowFactory, fPattern)
      }
    }

  }

  class ESqlQuery(ctx: ESqlContext,
                  queryParameters: List[ESqlQueryParameter],
                  queryResultFactory: ESqlQueryResult.Factory,
                  updateResultFactory: ESqlUpdateResult.Factory,
                  queryResultRowFactory: ESqlQueryResultRow.Factory,
                  fPattern: FPattern) {

    import fPattern._

    def params(pair: (String, Any), pairs: (String, Any)*): ESqlQuery = {
      val newQueryParameters = (pair :: pairs.toList).map { case (key, value) =>
        ESqlQueryParameter(key, value)
      }
      val updQueryParameters = queryParameters ::: newQueryParameters
      new ESqlQuery(ctx, updQueryParameters, queryResultFactory, updateResultFactory, queryResultRowFactory, fPattern)
    }

    def executeQuery(): ESqlQueryResult = {
      require(ctx.dataSource.isDefined, "dataSource should be defined")
      require(ctx.queryString.isDefined, "queryString should be defined")
      require(ctx.queryStringParser.isDefined, "queryStringParser should be defined")

      val queryAst = ctx.queryStringParser.get.parse(ctx.queryString.get)
      val paramTokens = queryAst.getParamTokens

      val rows = using(ctx.dataSource.get.getConnection) { connection =>
        using(connection.prepareStatement(queryAst.getNormalForm)) { preparedStatement =>
          setParameters(preparedStatement, paramTokens)
          using(preparedStatement.executeQuery()) { resultSet =>
            toRows(resultSet)
          }
        }
      }

      val updCtx = ctx.copy(queryRows = rows)

      queryResultFactory(updCtx)
    }

    private def toRows(resultSet: ResultSet): List[ESqlQueryResultRow] = {
      val updCtx = ctx.copy(resultSet = Some(resultSet))
      val result = ArrayBuffer.empty[ESqlQueryResultRow]
      while (resultSet.next()) {
        val row = queryResultRowFactory(updCtx)
        result.append(row)
      }
      result.toList
    }

    def executeUpdate(): ESqlUpdateResult = {
      require(ctx.dataSource.isDefined, "dataSource should be defined")
      require(ctx.queryString.isDefined, "queryString should be defined")
      require(ctx.queryStringParser.isDefined, "queryStringParser should be defined")

      val queryAst = ctx.queryStringParser.get.parse(ctx.queryString.get)
      val paramTokens = queryAst.getParamTokens

      val rowCount = using(ctx.dataSource.get.getConnection) { connection =>
        using(connection.prepareStatement(queryAst.getNormalForm)) { preparedStatement =>
          setParameters(preparedStatement, paramTokens)
          preparedStatement.executeUpdate()
        }
      }

      val updCtx = ctx.copy(updateRowCount = Some(rowCount))

      updateResultFactory(updCtx)
    }

    private def setParameters(preparedStatement: PreparedStatement, paramTokens: List[SqlQueryParamToken]) = {
      paramTokens.zipWithIndex.foreach { case (paramToken, index) =>
        queryParameters.find(p => p.key == paramToken.name) match {
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

    class Factory {

      def apply(ctx: ESqlContext): ESqlQueryResult = {
        new ESqlQueryResult(ctx)
      }
    }

  }

  class ESqlQueryResult(ctx: ESqlContext) {

    def asList[T: TypeTag]: List[T] = {
      ctx.queryRows.map(row => row.as[T])
    }
  }

  //

  object ESqlQueryResultRow {

    class Factory {

      def apply(ctx: ESqlContext): ESqlQueryResultRow = {
        new ESqlQueryResultRow(ctx)
      }
    }

  }

  class ESqlQueryResultRow(ctx: ESqlContext) {

    def as[T: TypeTag]: T = {
      require(ctx.resultSet.isDefined, "resultSet should be defined")

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

    class Factory {

      def apply(ctx: ESqlContext): ESqlUpdateResult = {
        new ESqlUpdateResult(ctx)
      }
    }

  }

  class ESqlUpdateResult(ctx: ESqlContext) {

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
