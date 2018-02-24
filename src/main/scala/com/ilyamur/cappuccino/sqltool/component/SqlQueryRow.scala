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

  def getData: Seq[Any] = data

  def asTyped[T: TypeTag](sqlTyped: SqlTyped[T], column: Int): T = {
    sqlTyped.getValue(this, column)
  }

  def like[T: TypeTag]: T = {
    val ttag = typeTag[T]
    val classSymbol = ttag.tpe.typeSymbol.asClass
    val isCaseClass = classSymbol.isCaseClass
    if (isCaseClass) {
      likeCaseClass[T](classSymbol)
    } else {
      likePredefClass[T](classSymbol)
    }
  }

  private def likeCaseClass[T](classSymbol: ClassSymbol): T = {
    val accessors = classSymbol.toType.members
      .collect {
        case m: MethodSymbol if m.isGetter && m.isPublic => m
      }
      .map { accessor =>
        (accessor.name.toString, accessor.info.resultType)
      }
      .toMap

    ???
  }

  private def likePredefClass[T: TypeTag](trgClassSymbol: ClassSymbol): T = {
    data.length match {
      case 0 =>
        throw report("no columns")
      case 1 =>
        val srcClassSymbol = currentMirror.classSymbol(data.head.getClass)
        sqlToolContext.postTran.get(srcClassSymbol) match {
          case Some(trgMap) =>
            trgMap.get(trgClassSymbol) match {
              case Some(t) =>
                t.asInstanceOf[(Any => T)].apply(data.head)
              case _ =>
                throw report(s"no mapping found for target type '${trgClassSymbol}'")
            }
          case _ =>
            throw report(s"no mapping found for source type '${trgClassSymbol}'")
        }
      case _ =>
        throw report("too many columns")
    }
  }

  // todo
  private def report(message: String = "<no message>"): Exception = throw new IllegalStateException(message)
}
