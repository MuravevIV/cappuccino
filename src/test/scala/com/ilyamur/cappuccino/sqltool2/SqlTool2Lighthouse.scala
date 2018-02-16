package com.ilyamur.cappuccino.sqltool2

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool2.component.SqlQueryResult2
import com.ilyamur.cappuccino.sqltool2.provider.CleanupProvider

object SqlToolLighthouse {

  val sqlTool: SqlTool2 = ???
  val connectionProvider: CleanupProvider[Connection] = ???
  val queryResult: SqlQueryResult2 = ???

  /*

  sqlTool.on(connectionProvider)
    .query("SELECT 1 AS one FROM dual")
    .executeQuery()


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
