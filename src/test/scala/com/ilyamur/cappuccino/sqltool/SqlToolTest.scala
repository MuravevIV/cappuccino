package com.ilyamur.cappuccino.sqltool

import com.ilyamur.cappuccino.sqltool.SqlTypes._
import org.h2.jdbcx.JdbcConnectionPool
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, Ignore, Matchers}

class SqlToolTest extends FunSpec
  with Matchers
  with MockitoSugar
  with BeforeAndAfterAll {

  val h2 = new H2()
  var connectionPool: JdbcConnectionPool = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    h2.initialize()
    connectionPool = h2.getConnectionPool
  }

  override def afterAll(): Unit = {
    connectionPool.dispose()

    super.afterAll()
  }

  describe("the SqlTool query functionality") {

    val sqlTool = new SqlTool()

    it("executes plain query and extracts primitive result") {

      val value = sqlTool.on(connectionPool)
        .query("select 'testText' as text from dual")
        .executeQuery()
        .asSingleTyped(stringTyped)

      value shouldBe "testText"
    }

    it("closes connection after execution") {

      val value = sqlTool.on(connectionPool)
        .query("select 'testText' as text from dual")
        .executeQuery()
        .asSingleTyped(stringTyped)

      connectionPool.getActiveConnections shouldBe 0
    }

    it("can extract list of primitive results") {

      val value = sqlTool.on(connectionPool)
        .query(
          """
            |select 'text1' as text from dual
            |union
            |select 'text2' as text from dual
          """.stripMargin
        )
        .executeQuery()
        .asListOfTyped(stringTyped)

      value shouldBe List("text1", "text2")
    }

    it("can extract single sql entity object") {

      val person = sqlTool.on(connectionPool)
        .query("select 'John' as name from dual")
        .executeQuery()
        .asSingle[SqlEntityPerson]

      val johnPerson = new SqlEntityPerson()
      johnPerson.name = "John"

      person shouldBe johnPerson
    }

    it("can extract list of sql entity objects") {

      val person = sqlTool.on(connectionPool)
        .query(
          """
            |select 'John' as name from dual
            |union
            |select 'Jane' as name from dual
          """.stripMargin
        )
        .executeQuery()
        .asListOf[SqlEntityPerson]

      val johnPerson = new SqlEntityPerson()
      johnPerson.name = "John"
      val janePerson = new SqlEntityPerson()
      janePerson.name = "Jane"

      person shouldBe List(johnPerson, janePerson)
    }

    it("can execute simple DDL") {

      val updateResult = sqlTool.on(connectionPool)
        .query("create table t (f varchar2)")
        .executeUpdate()

      updateResult
    }

    it("can execute simple DML") {

      sqlTool.on(connectionPool)
        .query("insert into person (name) values ('John')")
        .executeUpdate()
    }

    it("verifies updates on predicate") {

      sqlTool.on(connectionPool)
        .query("insert into person (name) values ('Jill')")
        .executeUpdate()
        .verifyUpdates(rowCount => rowCount > 0)
    }

    it("verifies any updates") {

      sqlTool.on(connectionPool)
        .query("insert into person (name) values ('Janet')")
        .executeUpdate()
        .verifyUpdates()
    }

    it("verifies single update") {

      sqlTool.on(connectionPool)
        .query("insert into person (name) values ('James')")
        .executeUpdate()
        .verifySingleUpdate()
    }

    it("executes select with parameter") {

      val testValue = sqlTool.on(connectionPool)
        .query("select <<param>> from dual")
        .params("param" -> "test")
        .executeQuery()
        .asSingleTyped(stringTyped)

      testValue shouldBe "test"
    }

    ignore("inserts and selects clob") {

      sqlTool.on(connectionPool)
        .query("insert into book (name, text) values (<<name>>, <<text>>)")
        .params(
          "name" -> "War and Peace",
          "text" -> "Some text."
        )
        // .withTransformer(H2ClobToStringTransformer)
        .executeUpdate()

      val bookText = sqlTool.on(connectionPool)
        .query("select text from book where name = <<name>>")
        .params("name" -> "War and Peace")
        .executeQuery()
        .asSingleTyped(stringTyped)

      bookText shouldBe "Some text."
    }

    it("can extract predef class with like-extractor") {

      sqlTool.registerPostQueryTransformer((s: String) => s)

      val name = sqlTool.on(connectionPool)
        .query("select 'John' as name from dual")
        .executeQuery()
        .like[String]

      name shouldBe "John"
    }

    it("can extract case class with like-extractor") {

      val person = sqlTool.on(connectionPool)
        .query("select 'John' as name from dual")
        .executeQuery()
        .like[CasePerson]

      person shouldBe CasePerson("John")
    }
  }
}




























