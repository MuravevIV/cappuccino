package com.ilyamur.cappuccino.sqltool

import javax.sql.ConnectionEvent

import org.h2.jdbcx.JdbcConnectionPool
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}
import com.ilyamur.cappuccino.sqltool.SqlTypes._
import org.mockito.Mockito
import org.mockito.Mockito.verify

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

  describe("the SqlTool query functionality") {

    val sqlTool = new SqlTool()

    it("executes plain query and extracts primitive result") {

      val value = sqlTool.on(dataSource)
        .query("select 'test_text' as text from dual")
        .executeQuery()
        .asSingleTyped(stringTyped)

      value shouldBe "test_text"
      dataSource.getActiveConnections shouldBe 0
    }
  }
}




























