package com.ilyamur.cappuccino.sqltoolv3

import javax.sql.DataSource

import com.softwaremill.macwire._

import scala.reflect.runtime.universe._

object Lighthouse {

  class Reflection {
  }

  //

  case class ESqlContext(queryParameters: List[ESqlQueryParameter] = List.empty) {
  }

  //

  object ESqlTool {

    class Factory {

      def apply(ctx: ESqlContext): ESqlTool = {
        new ESqlTool(ctx)
      }
    }

  }

  class ESqlTool(ctx: ESqlContext) {

    def onDataSource(dataSource: DataSource): ESqlExecutor = ???
  }

  //

  object ESqlExecutor {

    class Factory {

      def apply(ctx: ESqlContext): ESqlExecutor = {
        new ESqlExecutor(ctx)
      }
    }

  }

  class ESqlExecutor(ctx: ESqlContext) {

    def query(str: String): ESqlQuery = ???
  }

  //

  object ESqlQuery {

    class Factory {

      def apply(ctx: ESqlContext): ESqlQuery = {
        new ESqlQuery(ctx)
      }
    }

  }

  class ESqlQuery(ctx: ESqlContext) {

    def params(pair: (String, Any), pairs: (String, Any)*): ESqlQuery = {
      val newQueryParameters = (pair :: pairs.toList).map { case (key, value) =>
        new ESqlQueryParameter(key, value)
      }
      val newCtx = ctx.copy(queryParameters = ctx.queryParameters ::: newQueryParameters)
      new ESqlQuery(newCtx)
    }

    def executeQuery(): ESqlQueryResult = ???

    def executeUpdate(): ESqlUpdateResult = ???
  }

  //

  class ESqlQueryParameter(key: String, value: Any) {

  }

  //

  object ESqlQueryResult {

    class Factory {

      def apply(ctx: ESqlContext): ESqlQueryResult = {
        new ESqlQueryResult(ctx)
      }
    }

  }

  class ESqlQueryResult(ctx: ESqlContext) {

    def asList[T: TypeTag]: List[T] = ???
  }

  //

  object ESqlUpdateResult {

    class Factory {

      def apply(ctx: ESqlContext): ESqlUpdateResult = {
        new ESqlUpdateResult(ctx)
      }
    }

  }

  class ESqlUpdateResult(ctx: ESqlContext) {

    def verifyUpdates(): Unit = ???
  }

  //

  trait ESqlToolModule {

    lazy val sqlToolFactory = wire[ESqlTool.Factory]
    lazy val sqlExecutorFactory = wire[ESqlExecutor.Factory]
    lazy val sqlQueryFactory = wire[ESqlQuery.Factory]
    lazy val sqlResultFactory = wire[ESqlQueryResult.Factory]
  }
}
