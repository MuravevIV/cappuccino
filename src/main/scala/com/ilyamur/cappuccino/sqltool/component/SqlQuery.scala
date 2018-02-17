package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

class SqlQuery(queryString: String, dataSource: DataSource) {

  def executeQuery(): SqlQueryResult = {
    val connection = dataSource.getConnection
    try {
      val preparedStatement = connection.prepareStatement(queryString)
      val resultSet = preparedStatement.executeQuery()
      new SqlQueryResult(resultSet, dataSource)
    } finally {
      connection.close()
    }
  }
}
