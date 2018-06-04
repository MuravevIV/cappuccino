package com.ilyamur.cappuccino

import java.nio.charset.StandardCharsets

import com.typesafe.config.ConfigFactory
import org.apache.commons.io.IOUtils

import scala.util.Try

object Application2 {

  def main(args: Array[String]): Unit = {

    val paths = List(
      "entity/animal",
      "/entity/animal",
      "entity/animal.conf",
      "/entity/animal.conf"
    )

    paths
      .map { path =>
        ConfigFactory.parseResources(classOf[Application2].getClassLoader, path)
      }
      .foreach(println)

    paths
      .map { path =>
        ConfigFactory.parseResourcesAnySyntax(classOf[Application2].getClassLoader, path)
      }
      .foreach(println)

    paths
      .map { path =>
        Try {
          ConfigFactory.parseString(IOUtils.resourceToString(path, StandardCharsets.UTF_8))
        }
      }
      .foreach(println)

  }
}

class Application2