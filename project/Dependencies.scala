import sbt._

object Dependencies {

  val compileDependencies = Seq(
    "com.softwaremill.macwire" %% "macros" % "2.3.0",
    "com.typesafe" % "config" % "1.3.3",
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "com.google.guava" % "guava" % "24.0-jre",
    "org.apache.commons" % "commons-lang3" % "3.7"
  )

  val providedDependencies = Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scalacheck" % "scalacheck_2.12" % "1.13.5" % "test",
    "org.mockito" % "mockito-core" % "2.8.47" % "test",
    "com.h2database" % "h2" % "1.4.196" % Test
  )
}
