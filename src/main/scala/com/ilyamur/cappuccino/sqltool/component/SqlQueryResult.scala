package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.typed.SqlTyped

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

class SqlQueryResult(queryRows: ArrayBuffer[SqlQueryRow], dataSource: DataSource) extends Seq[SqlQueryRow] {

  override def length: Int = queryRows.length

  override def apply(idx: Int): SqlQueryRow = queryRows.apply(idx)

  override def iterator: Iterator[SqlQueryRow] = queryRows.iterator

  def asSingleTyped[T](sqlTyped: SqlTyped[T]): T = {
    sqlTyped.getSingleFrom(queryRows)
  }

  def asListOfTyped[T](sqlTyped: SqlTyped[T]): List[T] = {
    sqlTyped.getListFrom(queryRows)
  }

  def asSingle[T: TypeTag]: T = {
    queryRows.length match {
      case 0 => throw report()
      case 1 => getInstanceByQueryRow(queryRows.head)
      case _ => throw report()
    }
  }

  def asListOf[T: TypeTag]: List[T] = {
    queryRows
      .map { queryRow =>
        getInstanceByQueryRow(queryRow)
      }
      .toList
  }

  private def getInstanceByQueryRow[T: TypeTag](queryRow: SqlQueryRow): T = {

    // todo cache
    val constructor = getDefaultConstructor[T]

    val instance = constructor().asInstanceOf[T]
    instance match {
      case entity: SqlEntity[T] =>
        entity.fillOn(queryRow)
        entity.asInstanceOf[T]
      case _ =>
        throw report()
    }
  }

  private def getDefaultConstructor[T: TypeTag] = {
    val ttag = typeTag[T]
    currentMirror
      .reflectClass(ttag.tpe.typeSymbol.asClass)
      .reflectConstructor(
        ttag.tpe.members.filter(m =>
          m.isMethod && m.asMethod.isConstructor
        ).iterator.toSeq.head.asMethod
      )
  }

  // todo
  private def report(): Exception = ???
}
