package com.ilyamur.cappuccino.sqltool.typemapping

import com.ilyamur.cappuccino.sqltool.reflection.Reflection

import scala.reflect.runtime.universe._

class MultiFunction[OUT](multiFuncMap: Map[ClassSymbol, (_ => _)] = Map.empty) {

  // todo - lift up
  val reflection = new Reflection()

  def register[IN: TypeTag](func: IN => OUT): MultiFunction[OUT] = {
    val inSymbol = reflection.getClassSymbol[IN]
    val updMultiFuncMap = multiFuncMap + (inSymbol -> func)
    new MultiFunction[OUT](updMultiFuncMap)
  }

  def mApply[IN: TypeTag](arg: IN): OUT = {
    val inSymbol = reflection.getClassSymbol[IN]
    multiFuncMap.get(inSymbol) match {
      case Some(func) =>
        func.asInstanceOf[(IN => OUT)].apply(arg)
      case _ =>
        throw new IllegalStateException(s"Can not find function for input type '${inSymbol}'")
    }
  }
}
