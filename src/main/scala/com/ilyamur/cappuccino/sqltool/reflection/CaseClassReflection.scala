package com.ilyamur.cappuccino.sqltool.reflection

import scala.reflect.runtime.universe._

class CaseClassReflection[A] private[reflection](fields: Map[String, ClassSymbol],
                                                 fieldsOrder: List[String],
                                                 classSymbol: ClassSymbol,
                                                 reflection: Reflection) {

  private val reorderList = {

    val argNames = reflection.getConstructorArgNames(classSymbol)
    val idxMap = argNames.zipWithIndex.toMap

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

  def createInstance(args: Seq[Any]): A = {
    val constructorMirror = reflection.getConstructor(classSymbol)
    val reorderedArgs = reodredArgs(args)
    constructorMirror.apply(reorderedArgs: _*).asInstanceOf[A]
  }

  private def reodredArgs(args: Seq[Any]): Seq[Any] = {
    val buff = new Array[Any](args.length)
    reorderList.zip(args).foreach { case (idx, arg) =>
      buff(idx) = arg
    }
    buff.toList
  }
}
