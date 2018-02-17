package com.ilyamur.cappuccino.sqltool

object SqlToolLighthouse {

  val sqlTool = new SqlTool()
  val connection = ???

  sqlTool.on(connection)
    .query("SELECT 1 AS one FROM dual")
    .executeQuery()
    // .cache() // ??? - NO!

  /*

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
