package com.ilyamur.cappuccino.sqltool

import com.ilyamur.cappuccino.sqltool.SqlTypes.stringTyped
import com.ilyamur.cappuccino.sqltool.component.{SqlEntity, SqlQueryRow}

class Person() extends SqlEntity[Person] {

  var name: String = _

  override def fillOn(queryRow: SqlQueryRow): Unit = {
    this.name = queryRow.asTyped(stringTyped, 1)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Person]

  override def equals(other: Any): Boolean = other match {
    case that: Person =>
      (that canEqual this) &&
        name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
