package com.ilyamur.cappuccino.sqltool2.component

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool2.provider.CleanupProvider

class SqlExecutor2(connectionProvider: CleanupProvider[Connection]) {

  def query(queryString: String): SqlQuery2 = {
    new SqlQuery2(connectionProvider)
  }
}
