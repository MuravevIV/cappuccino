package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSetMetaData

class SqlQueryMetadata(resultSetMetaData: ResultSetMetaData) extends Seq[SqlCellMetadata] {

  private var cellMetadataArray: Seq[SqlCellMetadata] = _

  {
    cellMetadataArray = (1 to resultSetMetaData.getColumnCount).map { column =>
      new SqlCellMetadata(resultSetMetaData, column)
    }
  }

  override def length: Int = cellMetadataArray.length

  override def apply(idx: Int): SqlCellMetadata = cellMetadataArray.apply(idx)

  override def iterator: Iterator[SqlCellMetadata] = cellMetadataArray.iterator
}
