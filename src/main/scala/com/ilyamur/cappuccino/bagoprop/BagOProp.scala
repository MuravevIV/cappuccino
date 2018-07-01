package com.ilyamur.cappuccino.bagoprop

import com.typesafe.config.{Config, ConfigFactory, ConfigValue}

import scala.collection.JavaConverters._

class BagOProp(configProvider: ConfigProvider) {

  implicit val classLoader = getClass.getClassLoader

  def getConfig(configPath: String): Config = {
    val config = configProvider.getConfig(configPath)
    enrichConfig(config)
  }
  
  private def enrichConfig(config: Config): Config = {

    val g = configToMap(config)
      .groupBy { case (key, _) =>
        key == "__extends"
      }

    val complexConfigPart = g.getOrElse(true, Map.empty)
    complexConfigPart.get("__extends") match {
      case Some(configValue) =>
        val parentConfig = getConfig(configValue.unwrapped().toString)
        val ownConfig = mapToConfig(g.getOrElse(false, Map.empty))
        ownConfig.withFallback(parentConfig)
      case _ =>
        config
    }
  }

  private def configToMap(c: Config): Map[String, ConfigValue] = {
    c.entrySet().asScala.map(e => (e.getKey, e.getValue)).toMap
  }

  private def mapToConfig(m: Map[String, ConfigValue]): Config = {
    ConfigFactory.parseMap(m.asJava)
  }
}
