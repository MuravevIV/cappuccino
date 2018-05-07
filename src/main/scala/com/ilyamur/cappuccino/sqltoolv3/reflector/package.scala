package com.ilyamur.cappuccino.sqltoolv3

import com.ilyamur.cappuccino.sqltoolv3.sql.{ESqlCellMetadata, ESqlMetaData}
import com.softwaremill.macwire.wire

import scala.reflect.runtime._
import scala.reflect.runtime.universe._

package object reflector {

  class Reflector(reflectorOfCaseClassFactory: ReflectorOfCaseClass.Factory) {

    def forType[T: TypeTag](metaData: ESqlMetaData): ReflectorOfType = {
      val ttag = typeTag[T]
      // todo cache?
      if (ttag.tpe.typeSymbol.asClass.isCaseClass) {
        reflectorOfCaseClassFactory.apply(metaData, ttag.tpe)
      } else {
        // todo implement
        ???
      }
    }
  }

  trait ReflectorOfType {

    def createInstance[T: TypeTag](args: Seq[AnyRef]): T
  }

  object ReflectorOfCaseClass {

    type Factory = (ESqlMetaData, Type) => ReflectorOfCaseClass
  }

  class ReflectorOfCaseClass(metaData: ESqlMetaData, tpe: Type) extends ReflectorOfType {

    private val reorderList: List[Int] = {

      val argNames = getConstructorArgNames
      val idxMap = argNames.zipWithIndex.toMap
      val fieldsOrder = metaData.map { case ESqlCellMetadata(_, columnName) => columnName }.toList

      val validationSet = fieldsOrder.toSet.intersect(argNames.toSet)
      if ((argNames.size != fieldsOrder.size) || (argNames.size != validationSet.size)) {
        throw new IllegalArgumentException(s"Wrong reodering of fields: expected ${argNames}, got ${fieldsOrder}")
      }

      fieldsOrder.map { argName =>
        idxMap.get(argName) match {
          case Some(idx) =>
            idx
          case None =>
            throw new IllegalArgumentException(s"Can not find constructor argument ${argName} in fields order list")
        }
      }
    }

    override def createInstance[T: TypeTag](args: Seq[AnyRef]): T = {
      val rArgs = reodredArgs(args)
      getConstructor.apply(rArgs: _*).asInstanceOf[T]
    }

    private def reodredArgs(args: Seq[Any]): Seq[Any] = {
      val buff = new Array[Any](args.length)
      reorderList.zip(args).foreach { case (idx, arg) =>
        buff(idx) = arg
      }
      buff.toList
    }

    private def getConstructorArgNames: List[String] = {
      tpe.members
        .collect { case m: MethodSymbol if m.isConstructor && m.isPublic => m }
        .head
        .paramLists.flatMap(_.map(_.name.toString.toLowerCase))
    }

    private def getConstructor[T: TypeTag]: MethodMirror = {
      val ttag = typeTag[T]
      currentMirror.reflectClass(typeOf[T].typeSymbol.asClass).reflectConstructor(
        ttag.tpe.members.filter(m =>
          m.isMethod && m.asMethod.isConstructor
        ).iterator.toSeq.head.asMethod
      )
    }
  }

  trait ReflectorModule {

    lazy val reflector = wire[Reflector]
    lazy val reflectorOfCaseClassFactory = (_: ESqlMetaData, _: Type) => wire[ReflectorOfCaseClass]
  }

}
