package com.ilyamur.cappuccino

import com.ilyamur.cappuccino.bagoprop.{BagOProp, ConfigProvider}

object Application3 {

  def main(args: Array[String]): Unit = {
    val configProvider = new ConfigProvider()
    val bagOProp = new BagOProp(configProvider)
    val catConfig = bagOProp.getConfig("entity/cat.conf")
    println(catConfig)
  }
}