package com.ilyamur.cappuccino.sqltool.typed

class StringTyped extends SqlTyped[String] {

  override protected def extractValue(value: Any): Option[String] = value match {
    case stringValue: String =>
      Some(stringValue)
    case _ =>
      None
  }
}
