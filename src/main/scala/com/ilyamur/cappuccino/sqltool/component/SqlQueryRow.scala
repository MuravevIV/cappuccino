package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSet

import com.ilyamur.cappuccino.sqltool.SqlTool

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

  def getMetaData: SqlQueryMetadata = queryMetadata

  lazy val columnNameMap = queryMetadata.map { case SqlCellMetadata(_, columnName) => columnName }.zipWithIndex.toMap

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
    val ttag = typeTag[T]
    val classSymbol = ttag.tpe.typeSymbol.asClass
    likeNonCaseClass(classSymbol, columnName)
  }

  private def likeCaseClass[T](ttag: TypeTag[T]): T = {
    val ccd = sqlRuntimeMirror.createCaseClassData(ttag)
    val args = getArguments(ccd)
    ccd.runtimeConstructor.apply(args: _*).asInstanceOf[T]
  }

  private def getArguments[T](ccd: CaseClassData): List[Any] = {
    ccd.constructorArgNames.map { fieldName =>
      ccd.fieldsDict.get(fieldName) match {
        case Some(trgClassSymbol) =>
          likeNonCaseClass[Any](trgClassSymbol, fieldName)
        case _ =>
          report(s"Can not find accessor for consctructor argument '${fieldName}'")
      }
    }
  }

  private def likeNonCaseClass[T: TypeTag](trgClassSymbol: ClassSymbol): T = {
    data.length match {
      case 0 =>
        throw report("no columns")
      case 1 =>
        likeNonCaseClass(trgClassSymbol, 0)
      case _ =>
        throw report("too many columns")
    }
  }

  private def likeNonCaseClass[T: TypeTag](trgClassSymbol: ClassSymbol, columnName: String): T = {
    columnNameMap.get(columnName) match {
      case Some(columnIdx) =>
        likeNonCaseClass(trgClassSymbol, columnIdx)
      case _ =>
        throw report(s"Can not find column named '${columnName}'")
    }
  }

  private def likeNonCaseClass[T: TypeTag](trgClassSymbol: ClassSymbol, columnIdx: Int) = {
    val dataValue = data(columnIdx)
    val srcClassSymbol = currentMirror.classSymbol(dataValue.getClass)
    val function = getPostTranFunction(srcClassSymbol, trgClassSymbol)
    function(dataValue)
  }

  private def getPostTranFunction[T: TypeTag](srcClassSymbol: ClassSymbol, trgClassSymbol: ClassSymbol): (Any => T) = {
    sqlToolContext.postTran.get(srcClassSymbol) match {
      case Some(trgMap) =>
        trgMap.get(trgClassSymbol) match {
          case Some(t) =>
            t.asInstanceOf[(Any => T)]
          case _ =>
            throw report(s"no mapping found for transformation from '${srcClassSymbol}' to '${trgClassSymbol}'")
        }
      case _ =>
        throw report(s"no mapping found for transformation from '${srcClassSymbol}'")
    }
  }

  // todo
  private def report(message: String = "<no message>"): Exception = throw new IllegalStateException(message)
}
