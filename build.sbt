import sbt.Keys.{scalaVersion, _}

lazy val root = (project in file(".")).
  settings(
    organization := "com.ilyamur",
    name := "cappuccino",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.12.4",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "org.mockito" % "mockito-core" % "2.8.47" % "test",
      "org.scalacheck" % "scalacheck_2.12" % "1.13.5" % "test"
    )
  )
