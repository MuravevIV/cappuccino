package com.ilyamur.cappuccino.sqltool.reflection

import scala.reflect.{ManifestFactory, api}
import scala.reflect.runtime._
import scala.reflect.runtime.universe._

class Reflection {

  def getClassSymbol(clazz: Class[_]): ClassSymbol = {
    currentMirror.classSymbol(clazz)
  }

/*  def getClassSymbol(ttag: TypeTag[_]): ClassSymbol = {
    ttag.tpe.typeSymbol.asClass
  }*/

  def getClassSymbol[A: TypeTag]: ClassSymbol = {
    typeOf[A].typeSymbol.asClass
  }

  def forCaseClass[A: TypeTag](fieldsOrder: List[String]): CaseClassReflection[_] = {
    val classSymbol = getClassSymbol
    val fields = getFieldsDict(classSymbol)
    new CaseClassReflection(fields, fieldsOrder, classSymbol, this)
  }

  def getConstructorArgNames(classSymbol: ClassSymbol) = {
    classSymbol.toType.members
      .collect { case m: MethodSymbol if m.isConstructor && m.isPublic => m }
      .head
      .paramLists.flatMap(_.map(_.name.toString.toLowerCase))
  }

  def getFieldsDict(classSymbol: ClassSymbol): Map[String, ClassSymbol] = {
    classSymbol.toType.members
      .collect { case m: MethodSymbol if m.isGetter && m.isPublic => m }
      .map { accessor =>
        (accessor.name.toString.toLowerCase, accessor.info.resultType.typeSymbol.asClass)
      }
      .toMap
  }

  def getTypeTag(classSymbol: ClassSymbol): TypeTag[_] = {
    TypeTag(currentMirror, new api.TypeCreator {
      def apply[U <: api.Universe with Singleton](m: api.Mirror[U]) =
        if (m eq currentMirror) {
          classSymbol.selfType.asInstanceOf[U # Type]
        } else {
          throw new IllegalArgumentException(s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
        }
    })
  }

  def getConstructor(classSymbol: ClassSymbol) = {
    val ttag = getTypeTag(classSymbol)
    currentMirror.reflectClass(classSymbol).reflectConstructor(
      ttag.tpe.members.filter(m =>
        m.isMethod && m.asMethod.isConstructor
      ).iterator.toSeq.head.asMethod
    )
  }
}
