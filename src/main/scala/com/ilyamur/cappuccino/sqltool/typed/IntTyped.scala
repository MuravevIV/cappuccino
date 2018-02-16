package com.ilyamur.cappuccino.sqltool.typed

import java.sql.ResultSet

class IntTyped extends SqlTyped[Int] {

  def getValue(resultSet: ResultSet): Int = {
    resultSet.getInt(0)
  }
}
