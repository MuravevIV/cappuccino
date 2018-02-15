package com.ilyamur.cappuccino.wiring

import com.ilyamur.cappuccino.wiring.component.UserStatusReader

class Cappuccino(userStatusReader: UserStatusReader) {

  def run(args: Array[String]): Unit = {
    println("ok")
  }
}
