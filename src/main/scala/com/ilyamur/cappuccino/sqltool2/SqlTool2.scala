package com.ilyamur.cappuccino.sqltool2

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool2.component.SqlExecutor2
import com.ilyamur.cappuccino.sqltool2.provider.CleanupProvider

class SqlTool2 {

  def on(connectionProvider: CleanupProvider[Connection]): SqlExecutor2 = {
    new SqlExecutor2(connectionProvider)
  }
}
