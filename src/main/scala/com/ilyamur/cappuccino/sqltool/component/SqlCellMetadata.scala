package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSetMetaData

object SqlCellMetadata {

  def from(resultSetMetaData: ResultSetMetaData, column: Int): SqlCellMetadata = {
    val columnType = resultSetMetaData.getColumnType(column)
    val columnName = Option.apply(resultSetMetaData.getColumnName(column)).map(_.toLowerCase()).orNull
    new SqlCellMetadata(columnType, columnName)
  }
}

case class SqlCellMetadata(columnType: Int, columnName: String)
