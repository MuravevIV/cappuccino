package com.ilyamur.cappuccino.sqltool.typed

class IntTyped extends SqlTyped[Int] {

  override protected def extractValue(value: Any): Option[Int] = value match {
    case intValue: Int =>
      Some(intValue)
    case _ =>
      None
  }
}
