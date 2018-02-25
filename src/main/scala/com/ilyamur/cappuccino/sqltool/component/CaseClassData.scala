package com.ilyamur.cappuccino.sqltool.component

import scala.reflect.runtime.universe._

case class CaseClassData(constructorArgNames: List[String],
                         fieldsDict: Map[String, ClassSymbol],
                         runtimeConstructor: MethodMirror)
