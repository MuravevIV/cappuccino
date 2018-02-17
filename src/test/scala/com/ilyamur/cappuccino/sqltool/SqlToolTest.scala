package com.ilyamur.cappuccino.sqltool

import java.sql.Connection

import com.ilyamur.cappuccino.sqltool.component.SqlExecutor
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}

import java.sql.Connection
import java.sql.DriverManager

class SqlToolTest extends FunSpec
  with Matchers
  with MockitoSugar
  with BeforeAndAfterAll {

  var connection: Connection = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    Class.forName("org.h2.Driver")
    connection = DriverManager.getConnection("jdbc:h2:./target/h2", "sa", "")
  }

  override def afterAll(): Unit = {
    connection.close()

    super.afterAll()
  }

  describe("the SqlTool") {

    val sqlTool = new SqlTool()

    it("creates SqlExecutor on connection") {

      val sqlExecutor: SqlExecutor = sqlTool.on(connection)
    }
  }
}




























