package com.ilyamur.cappuccino.sqltool.component

import java.sql.ResultSet

import com.ilyamur.cappuccino.sqltool.SqlTool
import com.ilyamur.cappuccino.sqltool.typed.SqlTyped

import scala.reflect.runtime._
import scala.reflect.runtime.universe._

object SqlQueryRow {

  def from(resultSet: ResultSet, sqlToolContext: SqlTool.Context): SqlQueryRow = {
    val queryRow = new SqlQueryRow()
    queryRow.queryMetadata = SqlQueryMetadata.from(resultSet.getMetaData)
    val columnCount = resultSet.getMetaData.getColumnCount
    queryRow.data = (1 to columnCount).map { columnIndex =>
      resultSet.getObject(columnIndex)
    }
    queryRow.sqlToolContext = sqlToolContext
    queryRow
  }
}

class SqlQueryRow private() {

  private var queryMetadata: SqlQueryMetadata = _

  private var data: Seq[Any] = Seq.empty

  private var sqlToolContext: SqlTool.Context = SqlTool.Context()

  def getMetaData: SqlQueryMetadata = queryMetadata

  lazy val columnNameMap = queryMetadata.map { case SqlCellMetadata(_, columnName) => columnName }.zipWithIndex.toMap

  def getData: Seq[Any] = data

  def asTyped[T: TypeTag](sqlTyped: SqlTyped[T], column: Int): T = {
    sqlTyped.getValue(this, column)
  }

  def like[T: TypeTag]: T = {
    val ttag = typeTag[T]
    val classSymbol = ttag.tpe.typeSymbol.asClass
    val isCaseClass = classSymbol.isCaseClass
    if (isCaseClass) {
      likeCaseClass[T](ttag, classSymbol)
    } else {
      likeNonCaseClass[T](classSymbol)
    }
  }

  def like[T: TypeTag](columnName: String): T = {
    val ttag = typeTag[T]
    val classSymbol = ttag.tpe.typeSymbol.asClass
    likeNonCaseClass(classSymbol, columnName)
  }

  private def likeCaseClass[T](ttag: TypeTag[T], classSymbol: ClassSymbol): T = {
    val accessors = classSymbol.toType.members
      .collect {
        case m: MethodSymbol if m.isGetter && m.isPublic => m
      }
      .map { accessor =>
        (accessor.name.toString.toLowerCase, accessor.info.resultType)
      }
      .toMap

    val constructor = currentMirror.reflectClass(classSymbol).reflectConstructor(
      ttag.tpe.members.filter(m =>
        m.isMethod && m.asMethod.isConstructor
      ).iterator.toSeq.head.asMethod
    )

    val args = accessors.map { case (fieldName, trgFieldType) =>
      likeNonCaseClass[Any](trgFieldType.typeSymbol.asClass, fieldName)
    }.toList

    constructor.apply(args: _*).asInstanceOf[T]
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

  private def likeNonCaseClass[T: TypeTag](trgClassSymbol: universe.ClassSymbol, columnIdx: Int) = {
    val srcClassSymbol = currentMirror.classSymbol(data.head.getClass)
    sqlToolContext.postTran.get(srcClassSymbol) match {
      case Some(trgMap) =>
        trgMap.get(trgClassSymbol) match {
          case Some(t) =>
            t.asInstanceOf[(Any => T)].apply(data(columnIdx))
          case _ =>
            throw report(s"no mapping found for target type '${trgClassSymbol}'")
        }
      case _ =>
        throw report(s"no mapping found for source type '${trgClassSymbol}'")
    }
  }

  // todo
  private def report(message: String = "<no message>"): Exception = throw new IllegalStateException(message)
}
