package com.ilyamur.cappuccino.sqltool.module

import com.ilyamur.cappuccino.sqltool.component.SqlQueryRow
import com.ilyamur.cappuccino.wiring.Cappuccino
import com.ilyamur.cappuccino.wiring.component.{DatabaseAccess, SecurityFilter, UserFinder, UserStatusReader}
import com.softwaremill.macwire._

class SqlToolModule {

  /*lazy val cappuccino = wire[Cappuccino]
  lazy val theUserStatusReader = wire[UserStatusReader]
  lazy val theUserFinder = wire[UserFinder]
  lazy val theDatabaseAccess = wire[DatabaseAccess]
  lazy val theSecurityFilter = wire[SecurityFilter]*/

  class A()

  class B(s: String, a: A)

  class F extends (String => B) {
    override def apply(s: String): B = {

    }
  }

  class C(d: D) {
    def foo(): Unit = {
      val b = d.createB("s")
    }
  }

  class D(a: A) {
    def createB(s: String): B = new B(s, a)
  }
}
