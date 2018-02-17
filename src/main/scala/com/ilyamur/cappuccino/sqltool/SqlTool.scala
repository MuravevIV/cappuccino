package com.ilyamur.cappuccino.sqltool

import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.component.SqlExecutor

class SqlTool {

  def on(dataSource: DataSource): SqlExecutor = {
    new SqlExecutor(dataSource)
  }
}
