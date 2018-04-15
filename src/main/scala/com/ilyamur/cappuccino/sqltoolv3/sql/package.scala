package com.ilyamur.cappuccino.sqltoolv3

import java.sql.{PreparedStatement, ResultSet}
import java.util.concurrent.TimeUnit

import com.google.common.cache.{Cache, CacheBuilder, CacheLoader}
import com.ilyamur.cappuccino.sqltool.parser.{SqlQueryAst, SqlQueryParamToken, SqlQueryParser, SqlQueryTehnologiaParser}
import com.softwaremill.macwire._
import javax.sql.DataSource

import scala.reflect.runtime.universe._

package object sql {

  class FPattern {

    def using[C <: AutoCloseable, R](c: C)(f: C => R): R = {
      try {
        f(c)
      } finally {
        c.close()
      }
    }
  }

  //

  class ESqlTool(executorFactory: ESqlExecutor.Factory) {

    def onDataSource(dataSource: DataSource): ESqlExecutor = {
      executorFactory.apply(dataSource)
    }
  }

  //

  object ESqlExecutor {

    type Factory = (DataSource) => ESqlExecutor
  }

  class ESqlExecutor(dataSource: DataSource, queryFactory: ESqlQuery.Factory) {

    def query(queryString: String): ESqlQuery = {
      queryFactory.apply(dataSource, queryString, List.empty)
    }
  }

  //

  class CacheM {

    def apply()
  }

  //

  class CachedSqlQueryParser(childQueryParser: ChildSqlQueryParser) extends SqlQueryParser {

    private val cache: Cache[String, SqlQueryAst] = {
      // todo conf
      // todo JMX stats
      CacheBuilder.newBuilder()
        .maximumSize(1024)
        .expireAfterWrite(600, TimeUnit.SECONDS)
        .build()
    }

    override def parse(queryString: String): SqlQueryAst = {
      cache.get(queryString, { () =>
        childQueryParser.parse(queryString)
      })
    }
  }

  //

  object ESqlQuery {

    type Factory = (DataSource, String, ESqlQueryParameters) => ESqlQuery
  }

  class ESqlQuery(dataSource: DataSource,
                  queryString: String,
                  queryParameters: ESqlQueryParameters,
                  queryParser: SqlQueryParser,
                  queryResultFactory: ESqlQueryResult.Factory,
                  updateResultFactory: ESqlUpdateResult.Factory,
                  queryResultRowFactory: ESqlQueryResultRow.Factory,
                  fPattern: FPattern) {

    import fPattern._

    def params(pair: (String, Any), pairs: (String, Any)*): ESqlQuery = {
      val newQueryParameters = (pair :: pairs.toList).map { case (key, value) =>
        ESqlQueryParameter(key, value)
      }
      new ESqlQuery(dataSource, queryString, newQueryParameters,
        queryParser, queryResultFactory, updateResultFactory, queryResultRowFactory, fPattern)
    }

    def executeQuery(): ESqlQueryResult = {
      val rows = using(dataSource.getConnection) { connection =>
        val queryAst = queryParser.parse(queryString)
        using(connection.prepareStatement(queryAst.normalForm)) { preparedStatement =>
          setParameters(preparedStatement, queryAst.paramTokens)
          using(preparedStatement.executeQuery()) { resultSet =>
            toRows(resultSet)
          }
        }
      }
      queryResultFactory.apply(rows)
    }

    private def toRows(resultSet: ResultSet): ESqlQueryResultRows = {
      Stream
        .continually(resultSet)
        .takeWhile(_.next)
        .map(queryResultRowFactory.apply)
        .toList
    }

    def executeUpdate(): ESqlUpdateResult = {
      val rowCount = using(dataSource.getConnection) { connection =>
        val queryAst = queryParser.parse(queryString)
        using(connection.prepareStatement(queryAst.normalForm)) { preparedStatement =>
          setParameters(preparedStatement, queryAst.paramTokens)
          preparedStatement.executeUpdate()
        }
      }
      updateResultFactory.apply(rowCount)
    }

    private def setParameters(preparedStatement: PreparedStatement, paramTokens: List[SqlQueryParamToken]): Unit = {
      paramTokens.zipWithIndex.foreach { case (paramToken, index) =>
        queryParameters.find(_.key == paramToken.name) match {
          case Some(param) =>
            preparedStatement.setObject(index + 1, param.value)
          case None =>
            // todo
            throw new IllegalArgumentException()
        }
      }
    }
  }

  //

  case class ESqlQueryParameter(key: String, value: Any)

  type ESqlQueryParameters = List[ESqlQueryParameter]

  //

  object ESqlQueryResult {

    type Factory = (ESqlQueryResultRows) => ESqlQueryResult
  }

  class ESqlQueryResult(rows: ESqlQueryResultRows) {

    def asList[T: TypeTag]: List[T] = {
      rows.map(_.as[T])
    }
  }

  //

  class Reflector(reflectorOfCaseClassFactory: ReflectorOfCaseClass.Factory[_]) {

    def ofType[T: TypeTag]: ReflectorOfType[T] = {
      val ttag = typeTag[T]
      // todo cache?
      if (ttag.tpe.typeSymbol.asClass.isCaseClass) {
        reflectorOfCaseClassFactory.apply()
      } else {
      }
      ???
    }
  }

  trait ReflectorOfType[T] {

    def forResultSet(resultSet: ResultSet): ReflectorForResultSet[T]
  }

  object ReflectorOfCaseClass {

    type Factory[T] = () => ReflectorOfCaseClass[T]
  }

  class ReflectorOfCaseClass[T] extends ReflectorOfType[T] {

    override def forResultSet(resultSet: ResultSet): ReflectorForResultSet[T] = {
      resultSet.getMetaData
      ???
    }

    def getConstructor(classSymbol: ClassSymbol): MethodMirror = {
      /*val ttag = getTypeTag(classSymbol)
      currentMirror.reflectClass(classSymbol).reflectConstructor(
        ttag.tpe.members.filter(m =>
          m.isMethod && m.asMethod.isConstructor
        ).iterator.toSeq.head.asMethod
      )*/
      ???
    }
  }

  class ReflectorForResultSet[T] {

    def applyConstructor: T = ???
  }

  //

  type ESqlQueryResultRows = List[ESqlQueryResultRow]

  object ESqlQueryResultRow {

    type Factory = (ResultSet) => ESqlQueryResultRow
  }

  class ESqlQueryResultRow(resultSet: ResultSet,
                           reflector: Reflector) {

    def as[T: TypeTag]: T = {
      reflector.ofType[T].forResultSet(resultSet).applyConstructor
    }
  }

  //

  object ESqlUpdateResult {

    type Factory = (Int) => ESqlUpdateResult
  }

  class ESqlUpdateResult(rowCount: Int) {

    def verifyUpdates(): Unit = {
      // todo
    }
  }

  //

  trait ESqlToolModule {

    lazy val fPattern = wire[FPattern]
    lazy val reflector = wire[Reflector]

    lazy val childQueryParser: ChildSqlQueryParser = wire[SqlQueryTehnologiaParser]
    lazy val cachedQueryParser: SqlQueryParser = wire[CachedSqlQueryParser]

    lazy val tool = wire[ESqlTool]
    lazy val executorFactory = (_: DataSource) => wire[ESqlExecutor]
    lazy val queryFactory = (_: DataSource, _: String, _: ESqlQueryParameters) => wire[ESqlQuery]
    lazy val queryResultFactory = (_: ESqlQueryResultRows) => wire[ESqlQueryResult]
    lazy val queryResultRowFactory = (_: ResultSet) => wire[ESqlQueryResultRow]
    lazy val updateResultFactory = (_: Int) => wire[ESqlUpdateResult]
  }

}