package com.ilyamur.cappuccino.sqltool.converter

import java.sql.ResultSet

trait SqlTyped[T] {

  def getSingleFrom(resultSet: ResultSet): T

  def getListFrom(resultSet: ResultSet): List[T]

  protected def assertNoMoreRows(resultSet: ResultSet) = {
    if (resultSet.next()) {
      throw new IllegalStateException("Expected no more rows from a ResultSet: " + report(resultSet))
    }
  }

  protected def report(resultSet: ResultSet): String = ???
}
