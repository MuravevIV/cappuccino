package com.ilyamur.cappuccino.sqltool

import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.component.SqlExecutor

import scala.reflect.runtime.universe._

object SqlTool {

  case class Context(preTran: Map[ClassSymbol, Map[ClassSymbol, (_ => _)]] = Map.empty,
                     postTran: Map[ClassSymbol, Map[ClassSymbol, (_ => _)]] = Map.empty)

}

class SqlTool {

  private var ctx = SqlTool.Context(Map.empty)

  def registerPostQueryTransformer[A: TypeTag, B: TypeTag](t: A => B) = {
    val clazzA = typeOf[A].typeSymbol.asClass
    val clazzB = typeOf[B].typeSymbol.asClass
    val trgMap = ctx.postTran.getOrElse(clazzA, Map.empty) + (clazzB -> t)
    ctx = ctx.copy(postTran = ctx.postTran + (clazzA -> trgMap))
  }

  def on(dataSource: DataSource): SqlExecutor = {
    new SqlExecutor(dataSource, ctx)
  }
}
