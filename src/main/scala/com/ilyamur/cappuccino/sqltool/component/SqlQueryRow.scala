package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSet

import com.ilyamur.cappuccino.sqltool.typed.SqlTyped

import scala.reflect.runtime.universe._

object SqlQueryRow {

  def from(resultSet: ResultSet, transformers: List[_] = List.empty): SqlQueryRow = {
    val queryRow = new SqlQueryRow()
    queryRow.queryMetadata = SqlQueryMetadata.from(resultSet.getMetaData)
    val columnCount = resultSet.getMetaData.getColumnCount
    queryRow.data = (1 to columnCount).map { columnIndex =>
      resultSet.getObject(columnIndex)
    }
    queryRow
  }
}

class SqlQueryRow private() {

  private var queryMetadata: SqlQueryMetadata = _

  private var data: Seq[Any] = Seq.empty

  def getMetaData: SqlQueryMetadata = queryMetadata

  def getData: Seq[Any] = data

  def asTyped[T: TypeTag](sqlTyped: SqlTyped[T], column: Int): T = {
    sqlTyped.getValue(this, column)
  }
}
