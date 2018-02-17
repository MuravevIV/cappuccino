package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

class SqlQuery(queryString: String, dataSource: DataSource) {

  def execute(): SqlQueryResult = {
    val preparedStatement = dataSource.getConnection.prepareStatement(queryString)
    val resultSet = preparedStatement.executeQuery()

    new SqlQueryResult(resultSet, dataSource)
  }
}
