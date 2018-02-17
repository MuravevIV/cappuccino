package com.ilyamur.cappuccino.sqltool.typed

class BooleanTyped extends SqlTyped[Boolean] {

  override protected def extractValue(value: Any): Option[Boolean] = value match {
    case booleanValue: Boolean =>
      Some(booleanValue)
    case _ =>
      None
  }
}
