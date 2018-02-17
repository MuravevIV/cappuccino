package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSetMetaData

object SqlQueryMetadata {

  def from(resultSetMetaData: ResultSetMetaData): SqlQueryMetadata = {
    new SqlQueryMetadata(resultSetMetaData)
  }
}

class SqlQueryMetadata(resultSetMetaData: ResultSetMetaData) extends Seq[SqlCellMetadata] {

  private var cellMetadataArray: Seq[SqlCellMetadata] = _

  {
    cellMetadataArray = (1 to resultSetMetaData.getColumnCount).map { column =>
      SqlCellMetadata.from(resultSetMetaData, column)
    }
  }

  override def length: Int = cellMetadataArray.length

  override def apply(idx: Int): SqlCellMetadata = cellMetadataArray.apply(idx)

  override def iterator: Iterator[SqlCellMetadata] = cellMetadataArray.iterator
}
