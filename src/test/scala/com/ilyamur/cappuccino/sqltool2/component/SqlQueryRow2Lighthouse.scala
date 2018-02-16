package com.ilyamur.cappuccino.sqltool2.component

import com.ilyamur.cappuccino.sqltool.typed.SqlTyped

object SqlQueryRow2Lighthouse {

  val qres: SqlQueryRow2 = ???
  val intTyped: SqlTyped[Int] = ???

  val number = qres.asTyped(intTyped)
}
