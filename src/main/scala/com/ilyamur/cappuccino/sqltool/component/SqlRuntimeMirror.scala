package com.ilyamur.cappuccino.sqltool.component

import com.google.common.cache.{CacheBuilder, CacheLoader}

import scala.reflect.runtime._
import scala.reflect.runtime.universe._

class SqlRuntimeMirror {

  private val cache = CacheBuilder.newBuilder()
    .build(new CacheLoader[TypeTag[_], CaseClassData]() {
      override def load(ttag: TypeTag[_]): CaseClassData = {
        internalCreateCaseClassData(ttag)
      }
    })

  def createCaseClassData[T](ttag: TypeTag[T]) = {
    cache.get(ttag)
  }

  private def internalCreateCaseClassData[T](ttag: TypeTag[T]) = {
    val classSymbol = ttag.tpe.typeSymbol.asClass
    CaseClassData(
      constructorArgNames = getConstructorArgNames(classSymbol),
      fieldsDict = getFieldsDict(classSymbol),
      runtimeConstructor = getRuntimeConstructor(ttag, classSymbol)
    )
  }

  private def getConstructorArgNames[T](classSymbol: ClassSymbol) = {
    classSymbol.toType.members
      .collect { case m: MethodSymbol if m.isConstructor && m.isPublic => m }
      .head
      .paramLists.flatMap(_.map(_.name.toString.toLowerCase))
  }

  private def getFieldsDict[T](classSymbol: ClassSymbol) = {
    classSymbol.toType.members
      .collect { case m: MethodSymbol if m.isGetter && m.isPublic => m }
      .map { accessor =>
        (accessor.name.toString.toLowerCase, accessor.info.resultType.typeSymbol.asClass)
      }
      .toMap
  }

  private def getRuntimeConstructor[T](ttag: TypeTag[T], classSymbol: ClassSymbol) = {
    currentMirror.reflectClass(classSymbol).reflectConstructor(
      ttag.tpe.members.filter(m =>
        m.isMethod && m.asMethod.isConstructor
      ).iterator.toSeq.head.asMethod
    )
  }
}
