package com.ilyamur.cappuccino.sqltool.typed

import com.ilyamur.cappuccino.sqltool.component.SqlQueryRow

class StringTyped extends SqlTyped[String] {

  override def getValue(queryRow: SqlQueryRow, idx: Int): String = {
    val value = queryRow.getData(idx)
    extractValue(value) match {
      case Some(typedValue) =>
        typedValue
      case _ =>
        throw report()
    }
  }

  private def extractValue(value: Any) = value match {
    case stringValue: String =>
      Some(stringValue)
    case _ =>
      None
  }

  private def report(): Exception = ???
}
