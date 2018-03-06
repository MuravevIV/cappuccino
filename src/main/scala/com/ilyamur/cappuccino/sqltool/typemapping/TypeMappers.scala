package com.ilyamur.cappuccino.sqltool.typemapping

import com.ilyamur.cappuccino.sqltool.reflection.Reflection

import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

class TypeMappers(mFunctionMap: Map[ClassSymbol, MultiFunction[_]] = Map.empty) {

  // todo - lift up
  val reflection = new Reflection()

  def register[IN: TypeTag, OUT: TypeTag](func: IN => OUT): TypeMappers = {
    val outSymbol = reflection.getClassSymbol[OUT]
    val mFunction = mFunctionMap
      .getOrElse(outSymbol, new MultiFunction[OUT]())
      .asInstanceOf[MultiFunction[OUT]]
      .register(func)
    val updMFunctionMap = mFunctionMap + (outSymbol -> mFunction)
    new TypeMappers(updMFunctionMap)
  }

  def forOutputType[OUT: TypeTag]: MultiFunction[OUT] = {
    val outSymbol = reflection.getClassSymbol[OUT]
    forOutputClassSymbol(outSymbol)
  }

  def forOutputClassSymbol[OUT](outSymbol: ClassSymbol) = {
    mFunctionMap.get(outSymbol) match {
      case Some(m) =>
        m.asInstanceOf[MultiFunction[OUT]]
      case _ =>
        throw new IllegalStateException(s"Can not find multi-function for output type '$outSymbol'")
    }
  }
}
