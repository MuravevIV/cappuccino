package com.ilyamur.cappuccino.sqltool.typed

class FloatTyped extends SqlTyped[Float] {

  override protected def extractValue(value: Any): Option[Float] = value match {
    case floatValue: Float =>
      Some(floatValue)
    case _ =>
      None
  }
}
