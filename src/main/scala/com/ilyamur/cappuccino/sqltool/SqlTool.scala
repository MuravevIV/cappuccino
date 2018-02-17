package com.ilyamur.cappuccino.sqltool

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool.component.SqlExecutor

class SqlTool {
  
  def on(connection: Connection): SqlExecutor = {
    new SqlExecutor(connection)
  }
}
