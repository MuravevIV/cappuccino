package com.ilyamur.cappuccino.sqltool.component

import java.sql.Connection

class SqlQuery(queryString: String, connection: Connection) {

  def execute(): SqlQueryResult = {
    val preparedStatement = connection.prepareStatement(queryString)
    val resultSet = preparedStatement.executeQuery()

    new SqlQueryResult(resultSet, connection)
  }
}
