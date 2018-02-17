package com.ilyamur.cappuccino.sqltool.component

import java.sql.{Connection, ResultSet}
import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.typed.SqlTyped

class SqlQueryResult(resultSet: ResultSet, dataSource: DataSource) {

  def asSingleTyped[T](sqlTyped: SqlTyped[T]): T = {
    sqlTyped.getSingleFrom(resultSet)
  }

  def asListOfTyped[T](sqlTyped: SqlTyped[T]): List[T] = {
    sqlTyped.getListFrom(resultSet)
  }
}
