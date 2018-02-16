package com.ilyamur.cappuccino.sqltool2.component

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool2.provider.CleanupProvider

class SqlQuery2(connectionProvider: CleanupProvider[Connection]) {

  def executeQuery(): SqlQueryResult2 = {
    val connection = connectionProvider()
    val queryResult = new SqlQueryResult2()
    connectionProvider.cleanup()
    queryResult
  }
}
