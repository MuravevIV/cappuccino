package com.ilyamur.cappuccino.sqltool.component

class SqlUpdateResult(rowCount: Int) {


  /*

      .verifyUpdates()
    .verifySingleUpdate()
    .verifyUpdates(updateCount => updateCount == 1) // same as above

   */

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
