package com.ilyamur.cappuccino.sqltool

import java.io.{File, FileReader}
import javax.sql.DataSource

import org.h2.jdbcx.JdbcConnectionPool
import org.h2.tools.RunScript

class H2 {

  Class.forName("org.h2.Driver")

  private val H2_FILE_LOCATION = "target/h2.mv.db"
  private val CONNECTION_URL = "jdbc:h2:./target/h2"
  private val CONNECTION_USER = "sa"
  private val CONNECTION_PASSWORD = ""

  val INITIAL_SCRIPT_RESOURCE_LOCATION = "./h2_initial_script.sql"

  private var connectionPool: JdbcConnectionPool = _

  def initialize(): Unit = {
    new File(H2_FILE_LOCATION).delete()
    connectionPool = JdbcConnectionPool.create(CONNECTION_URL, CONNECTION_USER, CONNECTION_PASSWORD)
    setInitialTestData()
  }

  def getConnectionPool: JdbcConnectionPool = connectionPool

  def setInitialTestData(): Unit = {
    val connection = connectionPool.getConnection
    try {
      val url = getClass.getResource("/h2_initial_script.sql")
      RunScript.execute(connection, new FileReader(url.getFile))
      connection.commit()
    } finally {
      connection.close()
    }
  }
}
