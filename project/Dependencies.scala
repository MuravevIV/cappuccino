import sbt._

object Dependencies {

  val compileDependencies = Seq(
    "com.softwaremill.macwire" %% "macros" % "2.3.0"
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    "org.scalacheck" % "scalacheck_2.12" % "1.13.5" % "test",
    "org.mockito" % "mockito-core" % "2.8.47" % "test"
  )
}
