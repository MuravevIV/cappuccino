package com.ilyamur.cappuccino.sqltool

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool.component.SqlExecutor
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}
import java.sql.Connection
import java.sql.DriverManager
import javax.sql.DataSource

import org.h2.jdbcx.JdbcConnectionPool

class SqlToolTest extends FunSpec
  with Matchers
  with MockitoSugar
  with BeforeAndAfterAll {

  Class.forName("org.h2.Driver")

  val CONNECTION_URL = "jdbc:h2:./target/h2"
  val CONNECTION_USER = "sa"
  val CONNECTION_PASSWORD = ""

  var dataSource: JdbcConnectionPool = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    dataSource = JdbcConnectionPool.create("jdbc:h2:~/test", "sa", "sa")
  }

  override def afterAll(): Unit = {
    dataSource.dispose()

    super.afterAll()
  }

  describe("the SqlTool") {

    val sqlTool = new SqlTool()

    it("creates SqlExecutor on dataSource") {

      val sqlExecutor: SqlExecutor = sqlTool.on(dataSource)
    }
  }
}




























