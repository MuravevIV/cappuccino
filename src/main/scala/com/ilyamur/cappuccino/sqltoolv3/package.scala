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

  class ESqlTool(executorFactory: ESqlExecutor.Factory) {

    def onDataSource(dataSource: DataSource): ESqlExecutor = {
      executorFactory.apply(dataSource)
    }
  }

  //

  object ESqlExecutor {

    type Factory = (DataSource) => ESqlExecutor
  }

  class ESqlExecutor(dataSource: DataSource, queryFactory: ESqlQuery.Factory) {

    def query(queryString: String): ESqlQuery = {
      queryFactory.apply(dataSource, queryString, List.empty)
    }
  }

  //

  object ESqlQuery {

    type Factory = (DataSource, String, ESqlQueryParameters) => ESqlQuery
  }

  class ESqlQuery(dataSource: DataSource,
                  queryString: String,
                  queryParameters: ESqlQueryParameters,
                  queryParser: SqlQueryParser,
                  queryResultFactory: ESqlQueryResult.Factory,
                  updateResultFactory: ESqlUpdateResult.Factory,
                  queryResultRowFactory: ESqlQueryResultRow.Factory,
                  fPattern: FPattern) {

    import fPattern._

    def params(pair: (String, Any), pairs: (String, Any)*): ESqlQuery = {
      val newQueryParameters = (pair :: pairs.toList).map { case (key, value) =>
        ESqlQueryParameter(key, value)
      }
      new ESqlQuery(dataSource, queryString, newQueryParameters,
        queryParser, queryResultFactory, updateResultFactory, queryResultRowFactory, fPattern)
    }

    def executeQuery(): ESqlQueryResult = {

      val queryAst = queryParser.parse(queryString)
      val paramTokens = queryAst.getParamTokens

      val rows = using(dataSource.getConnection) { connection =>
        using(connection.prepareStatement(queryAst.getNormalForm)) { preparedStatement =>
          setParameters(preparedStatement, paramTokens)
          using(preparedStatement.executeQuery()) { resultSet =>
            toRows(resultSet)
          }
        }
      }

      queryResultFactory.apply(rows)
    }

    private def toRows(resultSet: ResultSet): List[ESqlQueryResultRow] = {
      val result = ArrayBuffer.empty[ESqlQueryResultRow]
      while (resultSet.next()) {
        val row = queryResultRowFactory.apply(resultSet)
        result.append(row)
      }
      result.toList
    }

    def executeUpdate(): ESqlUpdateResult = {

      val queryAst = queryParser.parse(queryString)
      val paramTokens = queryAst.getParamTokens

      val rowCount = using(dataSource.getConnection) { connection =>
        using(connection.prepareStatement(queryAst.getNormalForm)) { preparedStatement =>
          setParameters(preparedStatement, paramTokens)
          preparedStatement.executeUpdate()
        }
      }

      updateResultFactory.apply(rowCount)
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

  type ESqlQueryParameters = List[ESqlQueryParameter]

  //

  object ESqlQueryResult {

    type Factory = (ESqlQueryResultRows) => ESqlQueryResult
  }

  class ESqlQueryResult(rows: ESqlQueryResultRows) {

    def asList[T: TypeTag]: List[T] = {
      rows.map(row => row.as[T])
    }
  }

  //

  type ESqlQueryResultRows = List[ESqlQueryResultRow]

  object ESqlQueryResultRow {

    type Factory = (ResultSet) => ESqlQueryResultRow
  }

  class ESqlQueryResultRow(resultSet: ResultSet) {

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

      // val c = ctx

      ???
    }
  }

  //

  object ESqlUpdateResult {

    type Factory = (Int) => ESqlUpdateResult
  }

  class ESqlUpdateResult(rowCount: Int) {

    def verifyUpdates(): Unit = {
      // todo
    }
  }

  //

  trait ESqlToolModule {

    lazy val fPattern = wire[FPattern]
    lazy val queryParser = wire[SqlQueryTehnologiaParser]

    lazy val tool = wire[ESqlTool]
    lazy val executorFactory = (_: DataSource) => wire[ESqlExecutor]
    lazy val queryFactory = (_: DataSource, _: String, _: ESqlQueryParameters) => wire[ESqlQuery]
    lazy val queryResultFactory = (_: ESqlQueryResultRows) => wire[ESqlQueryResult]
    lazy val queryResultRowFactory = (_: ResultSet) => wire[ESqlQueryResultRow]
    lazy val updateResultFactory = (_: Int) => wire[ESqlUpdateResult]
  }

}
