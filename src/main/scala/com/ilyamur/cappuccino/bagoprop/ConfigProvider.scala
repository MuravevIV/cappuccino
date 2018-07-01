package com.ilyamur.cappuccino.bagoprop

import com.typesafe.config.{Config, ConfigFactory}

class ConfigProvider {

  def getConfig(configPath: String): Config = {
    ConfigFactory.parseResourcesAnySyntax(getClass.getClassLoader, configPath)
  }
}
