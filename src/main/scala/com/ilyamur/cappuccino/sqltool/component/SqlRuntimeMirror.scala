package com.ilyamur.cappuccino.sqltool.component

import com.google.common.cache.{CacheBuilder, CacheLoader}

import scala.reflect.api
import scala.reflect.api.{TypeCreator, Universe}
import scala.reflect.runtime.{universe, _}
import scala.reflect.runtime.universe._

class SqlRuntimeMirror {

  private val cache = CacheBuilder.newBuilder()
    .build(new CacheLoader[ClassSymbol, CaseClassData]() {
      override def load(classSymbol: ClassSymbol): CaseClassData = {
        val ttag = toTypeTag(classSymbol.selfType)
        internalCreateCaseClassData(ttag, classSymbol)
      }
    })

  private def toTypeTag(tpe: Type) = {
    val ttag = TypeTag(currentMirror, new TypeCreator {
      def apply[U <: Universe with Singleton](m: api.Mirror[U]) =
        if (m eq currentMirror) {
          tpe.asInstanceOf[U#Type]
        } else {
          throw new IllegalArgumentException(s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
        }
    })
    ttag
  }

  def createCaseClassData[T](ttag: TypeTag[T], classSymbol: ClassSymbol) = {
    cache.get(classSymbol)
    // internalCreateCaseClassData(ttag, classSymbol)
  }

  private def internalCreateCaseClassData[T](ttag: TypeTag[T], classSymbol: ClassSymbol) = {
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
