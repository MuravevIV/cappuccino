import sbt._

object Dependencies {

  val scalatestVersion = "3.0.1"
  val scalacheckVersion = "1.13.5"
  val mockitoVersion = "2.8.47"

  val macwireVersion = "2.3.0"

  val compileDependencies = Seq(
    "com.softwaremill.macwire" %% "macros" % macwireVersion
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % scalatestVersion % "test",
    "org.scalacheck" % "scalacheck_2.12" % scalacheckVersion % "test",
    "org.mockito" % "mockito-core" % mockitoVersion % "test"
  )
}
