package com.ilyamur.cappuccino.bagoprop

import com.typesafe.config.{Config, ConfigFactory, ConfigValue}

import scala.collection.JavaConverters._

class BagOProp(configProvider: ConfigProvider) {

  implicit val classLoader = getClass.getClassLoader

  def getConfig(configPath: String): Config = {
    val config = configProvider.getConfig(configPath)
    enrichConfig(config)
  }
  
  private def enrichConfig(c: Config): Config = {
    val configMap = configToMap(c)
      .map { case (key, configValue) =>
        if (key == "__extends") {
          val parentConfig = getConfig(configValue.unwrapped().toString)
          configToMap(parentConfig)
        } else {
          Map(key -> configValue)
        }
      }
      .flatten
      .toMap

    mapToConfig(configMap)
  }

  private def configToMap(c: Config): Map[String, ConfigValue] = {
    c.entrySet().asScala.map(e => (e.getKey, e.getValue)).toMap
  }

  private def mapToConfig(m: Map[String, ConfigValue]): Config = {
    ConfigFactory.parseMap(m.asJava)
  }
}
