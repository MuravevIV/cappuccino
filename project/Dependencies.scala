import sbt._

object Dependencies {

  val compileDependencies = Seq(
    "org.apache.spark" %% "spark-sql" % "2.3.0",
    "org.apache.zeppelin" %% "zeppelin-spark" % "0.7.3"
  )
}
