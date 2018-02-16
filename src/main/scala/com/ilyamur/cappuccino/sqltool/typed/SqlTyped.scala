package com.ilyamur.cappuccino.sqltool.typed

import java.sql.ResultSet

import scala.collection.mutable.ArrayBuffer

trait SqlTyped[T] {

  protected def getValue(resultSet: ResultSet): T

  def getValueFromObject(obj: Object): T = ???

  def getSingleFrom(resultSet: ResultSet): T = {
    resultSet.next()
    val value = getValue(resultSet)
    assertNoMoreRows(resultSet)
    value
  }

  def getListFrom(resultSet: ResultSet): List[T] = {
    val arrayBuffer = new ArrayBuffer[T]()
    while (resultSet.next()) {
      val value = getValue(resultSet)
      arrayBuffer.append(value)
    }
    arrayBuffer.toList
  }

  protected def assertNoMoreRows(resultSet: ResultSet) = {
    if (resultSet.next()) {
      throw new IllegalStateException("Expected no more rows from a ResultSet: " + report(resultSet))
    }
  }

  protected def report(resultSet: ResultSet): String = {
    resultSet.toString
  }
}
