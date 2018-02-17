package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSetMetaData

object SqlCellMetadata {

  def from(resultSetMetaData: ResultSetMetaData, column: Int): SqlCellMetadata = {
    val columnType = resultSetMetaData.getColumnType(column)
    new SqlCellMetadata(columnType)
  }
}

class SqlCellMetadata private(columnType: Int) {

}
