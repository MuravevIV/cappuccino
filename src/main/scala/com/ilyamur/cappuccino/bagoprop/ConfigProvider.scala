package com.ilyamur.cappuccino.bagoprop

import java.nio.charset.{Charset, StandardCharsets}

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.io.IOUtils

class ConfigProvider {

  def getConfig(configPath: String): Config = {
    val inputStream = getClass.getResourceAsStream("/" + configPath.stripPrefix("/"))
    try {
      val str = IOUtils.toString(inputStream, StandardCharsets.UTF_8)
      ConfigFactory.parseString(str)
    } finally {
      inputStream.close()
    }
  }
}
