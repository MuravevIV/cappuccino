package com.ilyamur.cappuccino.sqltool

import javax.sql.DataSource

import com.ilyamur.cappuccino.sqltool.component.SqlExecutor
import com.ilyamur.cappuccino.sqltool.typemapping.TypeMappers

import scala.reflect.runtime.universe._

object SqlTool {

  case class Context(preTran: Map[ClassSymbol, Map[ClassSymbol, (_ => _)]] = Map.empty,
                     var postTypeMappers: TypeMappers = new TypeMappers())

}

class SqlTool {

  private var ctx = SqlTool.Context(Map.empty)

  def registerPostQueryTransformer[A: TypeTag, B: TypeTag](func: A => B) = {
    // todo - un-var
    ctx.postTypeMappers = ctx.postTypeMappers.register(func)
  }

  def on(dataSource: DataSource): SqlExecutor = {
    new SqlExecutor(dataSource, ctx)
  }
}
