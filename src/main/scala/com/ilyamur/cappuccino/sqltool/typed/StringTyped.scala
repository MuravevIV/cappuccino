package com.ilyamur.cappuccino.sqltool.typed
import java.sql.ResultSet

class StringTyped extends SqlTyped[String] {

  override def getValue(resultSet: ResultSet): String = {
    resultSet.getString(1)
  }
}
