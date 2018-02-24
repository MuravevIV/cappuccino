package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.SqlTool

class SqlExecutor(dataSource: DataSource, ctx: SqlTool.Context) {

  def query(queryString: String): SqlQuery = {
    SqlQuery(queryString, dataSource, ctx)
  }
}
