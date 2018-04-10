package com.ilyamur.cappuccino.sqltool.component

import java.sql.Connection
import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.SqlTool
import com.ilyamur.cappuccino.sqltool.parser.SqlQueryTehnologiaParser

import scala.collection.mutable.ArrayBuffer

case class SqlQuery(queryString: String,
                    dataSource: DataSource,
                    sqlToolCtx: SqlTool.Context = SqlTool.Context(),
                    queryParameters: List[SqlQueryParameter] = List.empty) {

  private val parser = new SqlQueryTehnologiaParser()

  def executeQuery(): SqlQueryResult = {
    val connection = dataSource.getConnection
    try {
      val preparedStatement = prepareStatement(connection)
      val resultSet = preparedStatement.executeQuery()
      val queryRows: ArrayBuffer[SqlQueryRow] = new ArrayBuffer[SqlQueryRow]()
      while (resultSet.next()) {
        val queryRow = new SqlQueryRow(resultSet, sqlToolCtx)
        queryRows.append(queryRow)
      }
      new SqlQueryResult(queryRows, dataSource, sqlToolCtx)
    } finally {
      connection.close()
    }
  }

  def executeUpdate(): SqlUpdateResult = {
    val connection = dataSource.getConnection
    try {
      val preparedStatement = prepareStatement(connection)
      val rowCount = preparedStatement.executeUpdate()
      connection.commit()
      new SqlUpdateResult(rowCount)
    } finally {
      connection.close()
    }
  }

  private def prepareStatement(connection: Connection) = {
    val queryAst = parser.parse(queryString)
    val normalForm = queryAst.normalForm

    val preparedStatement = connection.prepareStatement(normalForm)

    val queryParameterMap = queryParameters.map { queryParameter =>
      (queryParameter.name, queryParameter.value)
    }.toMap

    queryAst.paramTokens.zipWithIndex.foreach { case (paramToken, idx) =>
      queryParameterMap.get(paramToken.name) match {
        case Some(value) =>
          preparedStatement.setObject(idx + 1, value)
        case _ =>
          throw report()
      }
    }
    preparedStatement
  }

  def params(pair: (String, Any), pairs: (String, Any)*): SqlQuery = {
    (pair :: pairs.toList).foldLeft(this) { (query, p) =>
      query.copy(queryParameters = query.queryParameters :+ (new SqlQueryParameter(p)))
    }
  }

  private def report(): Exception = ???
}
