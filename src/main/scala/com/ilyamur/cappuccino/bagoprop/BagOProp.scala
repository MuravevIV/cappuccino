package com.ilyamur.cappuccino.bagoprop

import com.typesafe.config.{Config, ConfigFactory, ConfigValue}

import scala.collection.JavaConverters._

class BagOProp(configProvider: ConfigProvider) {

  private val EXTENDS_PROPERTY = "__extends"

  def getConfig(configPath: String): Config = {
    val config = configProvider.getConfig(configPath)
    enrichConfig(config)
  }

  private def enrichConfig(config: Config): Config = {

    val configGroup = configToMap(config)
      .groupBy { case (key, _) =>
        key == EXTENDS_PROPERTY
      }

    val complexConfigPart = configGroup.getOrElse(true, Map.empty)
    complexConfigPart.get(EXTENDS_PROPERTY) match {
      case Some(configValue) =>
        val parentConfig = getConfig(configValue.unwrapped().toString)
        val ownConfig = mapToConfig(configGroup.getOrElse(false, Map.empty))
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
