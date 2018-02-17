package com.ilyamur.cappuccino.sqltool.component

import java.lang.reflect.Constructor
import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.typed.SqlTyped

import scala.collection.mutable.ArrayBuffer
import scala.reflect._

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

  def createInstance[T : TypeTag](args: AnyRef*)(ctor: Int = 0): T = {
    val tt = typeTag[T]
    currentMirror.reflectClass(tt.tpe.typeSymbol.asClass).reflectConstructor(
      tt.tpe.members.filter(m =>
        m.isMethod && m.asMethod.isConstructor
      ).iterator.toSeq(ctor).asMethod
    )(args: _*).asInstanceOf[T]
  }

  def asSingle[T: TypeTag]: T = {
    /*val clazz: Class[T] = classTag[T].runtimeClass.asInstanceOf[Class[T]]
    val constructor = clazz.getConstructors.head.asInstanceOf[Constructor[T]]
    val instance = constructor.newInstance()*/

    val instance = createInstance[T]()(0)

    instance match {
      case entity: SqlEntity[T] =>
        entity.fillOn(queryRows.head)
        entity.asInstanceOf[T]
      case _ => instance
    }
  }
}
