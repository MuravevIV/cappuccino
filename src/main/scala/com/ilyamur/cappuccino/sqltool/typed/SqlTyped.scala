package com.ilyamur.cappuccino.sqltool.typed

import com.ilyamur.cappuccino.sqltool.component.SqlQueryRow

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe._

trait SqlTyped[T] {

  private val HEAD_ROW_INDEX = 0
  private val HEAD_COLUMN_INDEX = 1

  def getValue(queryRow: SqlQueryRow, column: Int)(implicit tag: TypeTag[T]): T = {
    val value = queryRow.getData(column - 1)
    extractValue(value) match {
      case Some(typedValue) =>
        typedValue
      case _ =>
        throw report(typeOf[T], value)
    }
  }

  protected def extractValue(value: Any): Option[T]

  def getSingleFrom(queryRows: ArrayBuffer[SqlQueryRow])(implicit tag: TypeTag[T]): T = {
    val queryRow = queryRows(HEAD_ROW_INDEX)
    val result = getValue(queryRow, HEAD_COLUMN_INDEX)
    assertSingleRow(queryRows)
    result
  }

  def getListFrom(queryRows: ArrayBuffer[SqlQueryRow])(implicit tag: TypeTag[T]): List[T] = {
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

  // todo
  protected def report(t: Type, a: Any): Exception = {
    val className = t.typeSymbol.asClass.fullName
    throw new IllegalStateException(s"Can not transform value ${a} of type ${a.getClass.getCanonicalName} to type ${className}")
  }
}
