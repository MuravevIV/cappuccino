package com.ilyamur.cappuccino.sqltool.typed

class ShortTyped extends SqlTyped[Short] {

  override protected def extractValue(value: Any): Option[Short] = value match {
    case shortValue: Short =>
      Some(shortValue)
    case _ =>
      None
  }
}
