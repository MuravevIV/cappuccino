package com.ilyamur.cappuccino.sqltool.component

import java.sql.{Connection, PreparedStatement}
import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.parser.SqlQueryTehnologiaParser

import scala.collection.mutable.ArrayBuffer

case class SqlQuery(queryString: String,
                    dataSource: DataSource,
                    queryParameters: List[SqlQueryParameter] = List.empty,
                    transformers: List[_] = List.empty) {

  private val parser = new SqlQueryTehnologiaParser()

  def executeQuery(): SqlQueryResult = {
    val connection = dataSource.getConnection
    try {
      val preparedStatement = prepareStatement(connection)
      val resultSet = preparedStatement.executeQuery()
      val queryRows: ArrayBuffer[SqlQueryRow] = new ArrayBuffer[SqlQueryRow]()
      while (resultSet.next()) {
        val queryRow = SqlQueryRow.from(resultSet, transformers)
        queryRows.append(queryRow)
      }
      new SqlQueryResult(queryRows, dataSource)
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
    val normalForm = queryAst.getNormalForm

    val preparedStatement = connection.prepareStatement(normalForm)

    val queryParameterMap = queryParameters.map { queryParameter =>
      (queryParameter.name, queryParameter.value)
    }.toMap

    queryAst.getParamTokens.zipWithIndex.foreach { case (paramToken, idx) =>
      queryParameterMap.get(paramToken.name) match {
        case Some(value) =>
          preparedStatement.setObject(idx + 1, value)
        case _ =>
          throw report()
      }
    }
    preparedStatement
  }

  def params(pair: (String, String), pairs: (String, String)*): SqlQuery = {
    (pair :: pairs.toList).foldLeft(this) { (query, p) =>
      query.copy(queryParameters = query.queryParameters :+ SqlQueryParameter.from(p))
    }
  }

  def withTransformer[A, B](transformer: (A => B)) = {
    copy(transformers = transformers :+ transformer)
  }

  private def report(): Exception = ???
}
