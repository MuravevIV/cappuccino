package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.typed.SqlTyped

import scala.collection.mutable.ArrayBuffer

class SqlQueryResult(queryRows: ArrayBuffer[SqlQueryRow], dataSource: DataSource) extends Seq[SqlQueryRow] {

  override def length: Int = queryRows.length

  override def apply(idx: Int): SqlQueryRow = queryRows.apply(idx)

  override def iterator: Iterator[SqlQueryRow] = queryRows.iterator

  def asSingleTyped[T](sqlTyped: SqlTyped[T]): T = {
    sqlTyped.getSingleFrom(queryRows)
  }

  def asListOfTyped[T](sqlTyped: SqlTyped[T]): List[T] = {
    sqlTyped.getListFrom(queryRows)
  }
}
