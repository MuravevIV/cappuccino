package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSetMetaData

case class SqlCellMetadata(columnType: Int, columnName: String) {

  def this(resultSetMetaData: ResultSetMetaData, column: Int) = {
    this(
      columnType = resultSetMetaData.getColumnType(column),
      columnName = Option.apply(resultSetMetaData.getColumnName(column)).map(_.toLowerCase()).orNull
    )
  }
}
