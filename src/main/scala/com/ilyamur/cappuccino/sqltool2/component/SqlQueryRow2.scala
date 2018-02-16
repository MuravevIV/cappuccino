package com.ilyamur.cappuccino.sqltool2.component

import com.ilyamur.cappuccino.sqltool.typed.SqlTyped

class SqlQueryRow2(rowMetadata: RowMetadata, rowData: RowData) {

  def asTyped[T](sqlTyped: SqlTyped[T]): T = {
    val objectMetadata = rowMetadata.getMetadata(0)
    if (objectMetadata.isCompatibleWith(sqlTyped)) {
      val obj = rowData.getObject(0)
      sqlTyped.getValueFromObject(obj)
    } else {
      throw reportError()
    }
  }

  def reportError(): Exception = {
    ???
  }
}
