package com.ilyamur.cappuccino.sqltool.component

import java.sql.Connection

class SqlExecutor(connection: Connection) {

  def query(queryString: String): SqlQuery = {
    new SqlQuery(queryString, connection)
  }
}
