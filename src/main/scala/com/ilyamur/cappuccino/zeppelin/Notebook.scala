package com.ilyamur.cappuccino.zeppelin

import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.zeppelin.spark.ZeppelinContext

object Notebook {

  val z: ZeppelinContext = ???
  val sc: SparkContext = ???
  val sqlContext: SQLContext = ???
  val spark: SparkSession = ???
  import spark.implicits._

  //
}
