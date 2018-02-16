package com.ilyamur.cappuccino.sqltool2

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool2.provider.{CleanupProvider, ClosingProvider, NoopCleanupProvider}

object SqlToolLighthouse {

  val sqlTool = new SqlTool2()
  val connection = ???

  val getConnection: (() => Connection) = () => {
    ???
  }

  val connectionProvider1: CleanupProvider[Connection] = new ClosingProvider[Connection](getConnection)
  val connectionProvider2: CleanupProvider[Connection] = new NoopCleanupProvider[Connection](getConnection)

  /*

  sqlTool.on(connection)
    .query("SELECT 1 AS one FROM dual")
    .execute()


   row-based:
   .asTyped(intTyped)
   .asTyped(intTyped, "one")
   .as[SomeObject]

   set-based:
   .asSingleTyped(intTyped)
   .asListOfTyped(intTyped)
   .asSingle[SomeObject]
   .asListOf[SomeObject]

    */
}
