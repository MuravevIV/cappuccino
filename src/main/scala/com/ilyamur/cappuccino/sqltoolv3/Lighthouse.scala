package com.ilyamur.cappuccino.sqltoolv3

import java.io.Closeable
import java.sql.ResultSet

import com.softwaremill.macwire._
import javax.sql.DataSource

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe._

object Lighthouse {

  class Reflection {
  }

  //

  case class ESqlContext(queryParameters: List[ESqlQueryParameter] = List.empty,
                         dataSource: Option[DataSource] = None,
                         queryString: Option[String] = None,
                         resultSet: Option[ResultSet] = None,
                         queryRows: List[ESqlQueryResultRow] = List.empty,
                         updateRowCount: Option[Int] = None)

  //

  object ESqlTool {

    class Factory(executorFactory: ESqlExecutor.Factory) {

      def apply(ctx: ESqlContext): ESqlTool = {
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

      def apply(ctx: ESqlContext): ESqlQuery = {
        new ESqlQuery(ctx, queryResultFactory, updateResultFactory, queryResultRowFactory, fPattern)
      }
    }
  }

  class ESqlQuery(ctx: ESqlContext,
                  queryResultFactory: ESqlQueryResult.Factory,
                  updateResultFactory: ESqlUpdateResult.Factory,
                  queryResultRowFactory: ESqlQueryResultRow.Factory,
                  fPattern: FPattern) {

    import fPattern._

    def params(pair: (String, Any), pairs: (String, Any)*): ESqlQuery = {
      val newQueryParameters = (pair :: pairs.toList).map { case (key, value) =>
        new ESqlQueryParameter(key, value)
      }
      val newCtx = ctx.copy(queryParameters = ctx.queryParameters ::: newQueryParameters)
      new ESqlQuery(newCtx, queryResultFactory, updateResultFactory, queryResultRowFactory, fPattern)
    }

    def executeQuery(): ESqlQueryResult = {
      require(ctx.dataSource.isDefined, "dataSource should be defined")
      require(ctx.queryString.isDefined, "queryString should be defined")

      val rows = using(ctx.dataSource.get.getConnection) { connection =>
        using(connection.prepareStatement(ctx.queryString.get)) { preparedStatement =>
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

      val rowCount = using(ctx.dataSource.get.getConnection) { connection =>
        using(connection.prepareStatement(ctx.queryString.get)) { preparedStatement =>
          preparedStatement.executeUpdate()
        }
      }

      val updCtx = ctx.copy(updateRowCount = Some(rowCount))

      updateResultFactory(updCtx)
    }
  }

  //

  class ESqlQueryParameter(key: String, value: Any) {

  }

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

    lazy val toolFactory = wire[ESqlTool.Factory]
    lazy val executorFactory = wire[ESqlExecutor.Factory]
    lazy val queryFactory = wire[ESqlQuery.Factory]
    lazy val queryResultFactory = wire[ESqlQueryResult.Factory]
    lazy val queryResultRowFactory = wire[ESqlQueryResultRow.Factory]
    lazy val updateResultFactory = wire[ESqlUpdateResult.Factory]
    lazy val fPattern = wire[FPattern]
  }
}
