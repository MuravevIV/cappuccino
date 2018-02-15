package com.ilyamur.cappuccino.sqltool.component

import java.sql.{Connection, ResultSet}

import com.ilyamur.cappuccino.sqltool.converter.{IntTyped, SqlTyped}

class SqlQueryResult(resultSet: ResultSet, connection: Connection) {

  def asSingleTyped[T](sqlTyped: SqlTyped[T]): T = {
    sqlTyped.getSingleFrom(resultSet)
  }

  def asListOfTyped[T](sqlTyped: SqlTyped[T]): List[T] = {
    sqlTyped.getListFrom(resultSet)
  }
}
