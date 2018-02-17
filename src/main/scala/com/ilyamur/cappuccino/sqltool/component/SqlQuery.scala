package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

import scala.collection.mutable.ArrayBuffer

class SqlQuery(queryString: String, dataSource: DataSource) {

  def executeQuery(): SqlQueryResult = {
    val connection = dataSource.getConnection
    try {
      val preparedStatement = connection.prepareStatement(queryString)
      val resultSet = preparedStatement.executeQuery()
      val queryRows: ArrayBuffer[SqlQueryRow] = new ArrayBuffer[SqlQueryRow]()
      while (resultSet.next()) {
        val queryRow: SqlQueryRow = SqlQueryRow.from(resultSet)
        queryRows.append(queryRow)
      }
      new SqlQueryResult(queryRows, dataSource)
    } finally {
      connection.close()
    }
  }
}
