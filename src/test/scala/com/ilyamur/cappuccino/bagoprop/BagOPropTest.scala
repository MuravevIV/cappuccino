package com.ilyamur.cappuccino.bagoprop

import com.typesafe.config.{Config, ConfigFactory}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSuite, Matchers}

class BagOPropTest extends FunSuite
  with Matchers
  with MockitoSugar {

  val configProvider = mock[ConfigProvider]
  val bagoprop = new BagOProp(configProvider)

  val entityBaseConfigStringRaw = "{type: entity}"
  val entityBaseConfigStringEnriched = "{type: entity}"

  val entityAnimalConfigStringRaw = "{__extends: entity/base.conf, speed: 500}"
  val entityAnimalConfigStringEnriched = "{type: entity, speed: 500}"

  val entityCatConfigStringRaw = "{__extends: entity/animal.conf, speed: 700, sound: meow}"
  val entityCatConfigStringEnriched = "{type: entity, speed: 700, sound: meow}"

  when(configProvider.getConfig("entity/base.conf")).thenReturn(ConfigFactory.parseString(entityBaseConfigStringRaw))
  when(configProvider.getConfig("entity/animal.conf")).thenReturn(ConfigFactory.parseString(entityAnimalConfigStringRaw))
  when(configProvider.getConfig("entity/cat.conf")).thenReturn(ConfigFactory.parseString(entityCatConfigStringRaw))

  test("gets config w/o inheritance") {

    val config: Config = bagoprop.getConfig("entity/base.conf")

    config shouldEqual ConfigFactory.parseString(entityBaseConfigStringEnriched)
  }

  test("gets config with one-level inheritance") {

    val config: Config = bagoprop.getConfig("entity/animal.conf")

    config shouldEqual ConfigFactory.parseString(entityAnimalConfigStringEnriched)
  }

  test("gets config with two-level inheritance") {

    val config: Config = bagoprop.getConfig("entity/cat.conf")

    config shouldEqual ConfigFactory.parseString(entityCatConfigStringEnriched)
  }
}
