package com.ilyamur.cappuccino.sqltool.typed

class DoubleTyped extends SqlTyped[Double] {

  override protected def extractValue(value: Any): Option[Double] = value match {
    case doubleValue: Double =>
      Some(doubleValue)
    case _ =>
      None
  }
}
