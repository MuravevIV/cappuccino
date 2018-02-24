package com.ilyamur.cappuccino.sqltool.component

class SqlUpdateResult(rowCount: Int) {

  def verifyUpdates(p: Int => Boolean): Unit = {
    if (!p(rowCount)) {
      // todo
      throw new IllegalStateException()
    }
  }

  def verifyUpdates(): Unit = {
    verifyUpdates(rowCount => rowCount > 0)
  }

  def verifySingleUpdate(): Any = {
    verifyUpdates(rowCount => rowCount == 1)
  }
}
