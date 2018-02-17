package com.ilyamur.cappuccino.sqltool

import com.ilyamur.cappuccino.sqltool.typed._

object SqlTypes {

  val booleanTyped: BooleanTyped = new BooleanTyped()
  val byteTyped: ByteTyped = new ByteTyped()
  val shortTyped: ShortTyped = new ShortTyped()
  val intTyped: IntTyped = new IntTyped()
  val longTyped: LongTyped = new LongTyped()
  val floatTyped: FloatTyped = new FloatTyped()
  val doubleTyped: DoubleTyped = new DoubleTyped()
  val charTyped: CharTyped = new CharTyped()
  val stringTyped: StringTyped = new StringTyped()
}
