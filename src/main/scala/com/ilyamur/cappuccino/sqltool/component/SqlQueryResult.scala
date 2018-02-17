package com.ilyamur.cappuccino.sqltool.component

import javax.sql.DataSource

import com.google.common.cache.{CacheBuilder, CacheLoader}
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

  // todo - make global (service component?)
  val cachedConstructors = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .build(new CacheLoader[TypeTag[_], MethodMirror]() {
      def load(ttag: TypeTag[_]): MethodMirror = {
        currentMirror
          .reflectClass(ttag.tpe.typeSymbol.asClass)
          .reflectConstructor(
            ttag.tpe.members.filter(m =>
              m.isMethod && m.asMethod.isConstructor
            ).iterator.toSeq.head.asMethod
          )
      }
    })

  private def getInstanceByQueryRow[T: TypeTag](queryRow: SqlQueryRow): T = {
    val ttag = typeTag[T]
    val constructor = cachedConstructors.get(ttag)
    val instance = constructor().asInstanceOf[T]

    instance match {
      case entity: SqlEntity[T] =>
        entity.fillOn(queryRow)
        entity.asInstanceOf[T]
      case _ =>
        throw report()
    }
  }

  // todo
  private def report(): Exception = ???
}
