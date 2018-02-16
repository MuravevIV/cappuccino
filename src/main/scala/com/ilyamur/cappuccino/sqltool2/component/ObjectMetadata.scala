package com.ilyamur.cappuccino.sqltool2.component

import com.ilyamur.cappuccino.sqltool.typed.SqlTyped

class ObjectMetadata {

  def isCompatibleWith[T](sqlTyped: SqlTyped[T]): Boolean = ???
}
