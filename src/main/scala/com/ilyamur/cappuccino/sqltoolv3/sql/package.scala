package com.ilyamur.cappuccino.sqltoolv3

import java.sql.{PreparedStatement, ResultSet, ResultSetMetaData}

import com.ilyamur.cappuccino.sqltool.parser.{SqlQueryParamToken, SqlQueryParser}
import com.ilyamur.cappuccino.sqltoolv3.fpattern.{FPattern, FPatternModule}
import com.ilyamur.cappuccino.sqltoolv3.parser.ParserModule
import com.ilyamur.cappuccino.sqltoolv3.reflector.{Reflector, ReflectorModule}
import com.softwaremill.macwire._
import javax.sql.DataSource

import scala.reflect.runtime.universe._

package object sql {

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
      val rows = using(dataSource.getConnection) { connection =>
        using(connection.prepareStatement(queryAst.normalForm)) { preparedStatement =>
          setParameters(preparedStatement, queryAst.paramTokens)
          using(preparedStatement.executeQuery()) { resultSet =>
            toRows(resultSet)
          }
        }
      }
      queryResultFactory.apply(rows)
    }

    private def toRows(resultSet: ResultSet): ESqlQueryResultRows = {
      Stream.continually(resultSet)
        .takeWhile(_.next)
        .map(queryResultRowFactory.apply)
        .toList
    }

    def executeUpdate(): ESqlUpdateResult = {
      val queryAst = queryParser.parse(queryString)
      val rowCount = using(dataSource.getConnection) { connection =>
        using(connection.prepareStatement(queryAst.normalForm)) { preparedStatement =>
          setParameters(preparedStatement, queryAst.paramTokens)
          preparedStatement.executeUpdate()
        }
      }
      updateResultFactory.apply(rowCount)
    }

    private def setParameters(preparedStatement: PreparedStatement, paramTokens: List[SqlQueryParamToken]): Unit = {
      paramTokens.zipWithIndex.foreach { case (paramToken, index) =>
        queryParameters.find(_.key == paramToken.name) match {
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
      rows.map(_.as[T])
    }
  }

  //


  case class ESqlCellMetadata(columnType: Int, columnName: String) {

    def this(resultSetMetaData: ResultSetMetaData, column: Int) = {
      this(
        columnType = resultSetMetaData.getColumnType(column),
        columnName = Option.apply(resultSetMetaData.getColumnName(column)).map(_.toLowerCase()).orNull
      )
    }
  }

  class ESqlMetaData(resultSetMetaData: ResultSetMetaData) extends Seq[ESqlCellMetadata] {

    val cellMetadataArray = (1 to resultSetMetaData.getColumnCount).map { column =>
      new ESqlCellMetadata(resultSetMetaData, column)
    }

    val columnCount = resultSetMetaData.getColumnCount

    override def length: Int = cellMetadataArray.length

    override def apply(idx: Int): ESqlCellMetadata = cellMetadataArray.apply(idx)

    override def iterator: Iterator[ESqlCellMetadata] = cellMetadataArray.iterator
  }

  //

  type ESqlQueryResultRows = List[ESqlQueryResultRow]

  object ESqlQueryResultRow {

    type Factory = (ResultSet) => ESqlQueryResultRow
  }

  class ESqlQueryResultRow(resultSet: ResultSet, reflector: Reflector) {

    private val metaData = new ESqlMetaData(resultSet.getMetaData)
    private val data = (1 to metaData.columnCount).map { columnIndex =>
      resultSet.getObject(columnIndex)
    }

    def as[T: TypeTag]: T = {
      val ofType = reflector.forType[T](metaData)
      ofType.createInstance[T](data)
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

  trait ESqlToolModule extends FPatternModule with ReflectorModule with ParserModule {

    lazy val tool = wire[ESqlTool]
    lazy val executorFactory = (_: DataSource) => wire[ESqlExecutor]
    lazy val queryFactory = (_: DataSource, _: String, _: ESqlQueryParameters) => wire[ESqlQuery]
    lazy val queryResultFactory = (_: ESqlQueryResultRows) => wire[ESqlQueryResult]
    lazy val queryResultRowFactory = (_: ResultSet) => wire[ESqlQueryResultRow]
    lazy val updateResultFactory = (_: Int) => wire[ESqlUpdateResult]
  }

}
