package com.ilyamur.cappuccino.sqltool.typed

import com.ilyamur.cappuccino.sqltool.component.SqlQueryRow

import scala.collection.mutable.ArrayBuffer

trait SqlTyped[T] {

  private val HEAD_ROW_INDEX = 0
  private val HEAD_COLUMN_INDEX = 0

  def getValue(queryRow: SqlQueryRow, idx: Int): T = {
    val value = queryRow.getData(idx)
    extractValue(value) match {
      case Some(typedValue) =>
        typedValue
      case _ =>
        throw report()
    }
  }

  protected def extractValue(value: Any): Option[T]

  def getSingleFrom(queryRows: ArrayBuffer[SqlQueryRow]): T = {
    val queryRow = queryRows(HEAD_ROW_INDEX)
    val result = getValue(queryRow, HEAD_COLUMN_INDEX)
    assertSingleRow(queryRows)
    result
  }

  def getListFrom(queryRows: ArrayBuffer[SqlQueryRow]): List[T] = {
    queryRows
      .map(queryRow => getValue(queryRow, HEAD_COLUMN_INDEX))
      .toList
  }

  protected def assertSingleRow(queryRows: ArrayBuffer[SqlQueryRow]) = {
    if (queryRows.length > 1) {
      throw new IllegalStateException(report(queryRows))
    }
  }

  // todo
  protected def report(queryRows: ArrayBuffer[SqlQueryRow]): String = ???

  protected def report(): Exception = ???
}
