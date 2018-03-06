package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSet

import com.ilyamur.cappuccino.sqltool.SqlTool
import com.ilyamur.cappuccino.sqltool.reflection.{Reflection, SqlRuntimeMirror}

import scala.reflect.runtime._
import scala.reflect.runtime.universe._

class SqlQueryRow private() {

  def this(resultSet: ResultSet, sqlToolContext: SqlTool.Context) = {
    this()
    this.queryMetadata = new SqlQueryMetadata(resultSet.getMetaData)
    val columnCount = resultSet.getMetaData.getColumnCount
    this.data = (1 to columnCount).map { columnIndex =>
      resultSet.getObject(columnIndex)
    }
    this.sqlToolContext = sqlToolContext
    this.sqlRuntimeMirror = new SqlRuntimeMirror()
  }

  private var queryMetadata: SqlQueryMetadata = _

  private var data: Seq[Any] = Seq.empty

  private var sqlToolContext: SqlTool.Context = SqlTool.Context()

  private var sqlRuntimeMirror: SqlRuntimeMirror = _

  private var reflection = new Reflection()

  def getMetaData: SqlQueryMetadata = queryMetadata

  lazy val columnNameList = queryMetadata.map { case SqlCellMetadata(_, columnName) => columnName }.toList
  lazy val columnNameMap = columnNameList.zipWithIndex.toMap

  def getData: Seq[Any] = data

  def like[T: TypeTag]: T = {
    val ttag = typeTag[T]
    val classSymbol = ttag.tpe.typeSymbol.asClass
    if (classSymbol.isCaseClass) {
      likeCaseClass[T](ttag)
    } else {
      likeNonCaseClass[T](classSymbol)
    }
  }

  def like[T: TypeTag](columnName: String): T = {
    likeNonCaseClass(columnName)
  }

  private def likeCaseClass[T: TypeTag](ttag: TypeTag[T]): T = {
    val ccr = reflection.forCaseClass[T](columnNameList)

    val args: Map[String, ClassSymbol] = ccr.getFields

    val updData = data.zip(columnNameList).map { case (elem, name) =>
      args.get(name) match {
        case Some(outSymbol) =>
          val inSymbol = reflection.getClassSymbol(elem.getClass)
          sqlToolContext.postTypeMappers
            .forOutputClassSymbol[Any](outSymbol)
            .forInputClassSymbol(inSymbol)
            .apply(elem)
        case _ =>
          throw new IllegalStateException() // todo
      }
    }

    ccr.createInstance(updData)
  }

  private def likeNonCaseClass[T: TypeTag](trgClassSymbol: ClassSymbol): T = {
    data.length match {
      case 0 =>
        throw report("no columns")
      case 1 =>
        likeNonCaseClass(0)
      case _ =>
        throw report("too many columns")
    }
  }

  private def likeNonCaseClass[T: TypeTag](columnName: String): T = {
    columnNameMap.get(columnName) match {
      case Some(columnIdx) =>
        likeNonCaseClass(columnIdx)
      case _ =>
        throw report(s"Can not find column named '${columnName}'")
    }
  }

  private def likeNonCaseClass[T: TypeTag](columnIdx: Int) = {
    val dataValue = data(columnIdx)
    val srcClassSymbol = currentMirror.classSymbol(dataValue.getClass)

    sqlToolContext.postTypeMappers
      .forOutputType[T]
      .forInputClassSymbol(srcClassSymbol)
      .apply(dataValue)
  }

  // todo
  private def report(message: String = "<no message>"): Exception = throw new IllegalStateException(message)
}
