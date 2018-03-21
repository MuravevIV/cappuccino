import sbt._
import sbt.Keys._
import Dependencies._

lazy val root = (project in file("."))
  .settings(
      organization := "com.ilyamur",
      name := "cappuccino",
      version := "1.0-SNAPSHOT",
      scalaVersion := "2.12.4",
      libraryDependencies ++= compileDependencies ++ providedDependencies ++ testDependencies
  )
