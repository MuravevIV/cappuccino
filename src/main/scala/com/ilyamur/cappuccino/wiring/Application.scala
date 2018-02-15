package com.ilyamur.cappuccino.wiring

import com.ilyamur.cappuccino.wiring.module.ApplicationModule

object Application {

  def main(args: Array[String]): Unit = {
    val applicationModule = new ApplicationModule()
    applicationModule.cappuccino.run(args)
  }
}
