package com.ilyamur.cappuccino.sqltool.typed

class ByteTyped extends SqlTyped[Byte] {

  override protected def extractValue(value: Any): Option[Byte] = value match {
    case byteValue: Byte =>
      Some(byteValue)
    case _ =>
      None
  }
}
