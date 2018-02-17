package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

class SqlExecutor(dataSource: DataSource) {

  def query(queryString: String): SqlQuery = {
    new SqlQuery(queryString, dataSource)
  }
}
