package com.ilyamur.cappuccino.sqltool.typed

class CharTyped extends SqlTyped[Char] {

  override protected def extractValue(value: Any): Option[Char] = value match {
    case charValue: Char =>
      Some(charValue)
    case _ =>
      None
  }
}
