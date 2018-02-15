package com.ilyamur.cappuccino.wiring.module

import com.ilyamur.cappuccino.wiring.Cappuccino
import com.ilyamur.cappuccino.wiring.component._
import com.softwaremill.macwire._

class ApplicationModule {

  lazy val cappuccino = wire[Cappuccino]
  lazy val theUserStatusReader = wire[UserStatusReader]
  lazy val theUserFinder = wire[UserFinder]
  lazy val theDatabaseAccess = wire[DatabaseAccess]
  lazy val theSecurityFilter = wire[SecurityFilter]
}
