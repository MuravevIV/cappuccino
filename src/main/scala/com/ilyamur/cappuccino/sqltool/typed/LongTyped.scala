package com.ilyamur.cappuccino.sqltool.typed

class LongTyped extends SqlTyped[Long] {

  override protected def extractValue(value: Any): Option[Long] = value match {
    case longValue: Long =>
      Some(longValue)
    case _ =>
      None
  }
}
