import sbt._

object Dependencies {

  val macwireVersion = "2.3.0"
  val guavaVersion = "24.0-jre"

  val scalatestVersion = "3.0.1"
  val scalacheckVersion = "1.13.5"
  val mockitoVersion = "2.8.47"
  val h2Version = "1.4.196"

  val compileDependencies = Seq(
    "com.softwaremill.macwire" %% "macros" % macwireVersion,
    "com.google.guava" % "guava" % guavaVersion
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "org.scalacheck" % "scalacheck_2.12" % scalacheckVersion % "test",
    "org.mockito" % "mockito-core" % mockitoVersion % "test",
    "com.h2database" % "h2" % h2Version % Test
  )
}
