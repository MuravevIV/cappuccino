package com.ilyamur.cappuccino.sqltool2.component

import scala.collection.mutable.ArrayBuffer

class SqlQueryResult2 extends Seq[SqlQueryRow2] {

  private val parentSeq: Seq[SqlQueryRow2] = new ArrayBuffer[SqlQueryRow2]()

  override def length = parentSeq.length

  override def apply(idx: Int): SqlQueryRow2 = parentSeq.apply(idx)

  override def iterator = parentSeq.iterator
}
