package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.SqlTool

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe._

class SqlQueryResult(queryRows: ArrayBuffer[SqlQueryRow],
                     dataSource: DataSource,
                     sqlToolCtx: SqlTool.Context = SqlTool.Context()) extends Seq[SqlQueryRow] {

  override def length: Int = queryRows.length

  override def apply(idx: Int): SqlQueryRow = queryRows.apply(idx)

  override def iterator: Iterator[SqlQueryRow] = queryRows.iterator

  def like[T: TypeTag]: T = {
    queryRows.length match {
      case 0 => throw report("no rows")
      case 1 => queryRows.head.like[T]
      case _ => throw report("too many rows")
    }
  }

  def likeList[T: TypeTag]: List[T] = {
    queryRows.map(_.like[T]).toList
  }

  // todo
  private def report(message: String = "<no message>"): Exception = throw new IllegalStateException(message)
}
