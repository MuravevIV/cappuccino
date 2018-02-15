package com.ilyamur.cappuccino.sqltool.converter

import java.sql.ResultSet

import scala.collection.mutable.ArrayBuffer

class IntTyped extends SqlTyped[Int] {

  override def getSingleFrom(resultSet: ResultSet): Int = {
    resultSet.next()
    val result = resultSet.getInt(0)
    assertNoMoreRows(resultSet)
    result
  }

  def getListFrom(resultSet: ResultSet): List[Int] = {
    val arrayBuffer = new ArrayBuffer[Int]()
    while (resultSet.next()) {
      arrayBuffer.insert(resultSet.getInt(0))
    }
    arrayBuffer.toList
  }
}
