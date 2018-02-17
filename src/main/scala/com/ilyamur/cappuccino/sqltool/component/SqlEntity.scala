package com.ilyamur.cappuccino.sqltool.component

trait SqlEntity[T] {

  def fillOn(queryRow: SqlQueryRow): Unit
}
