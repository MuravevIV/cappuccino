package com.ilyamur.cappuccino.sqltool

import org.h2.jdbcx.JdbcConnectionPool
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}

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

    val sqlTool = SqlToolkit.getDefaultTool

    it("executes plain query and extracts primitive result") {

      val value = sqlTool.on(connectionPool)
        .query("select 'testText' as text from dual")
        .executeQuery()
        .like[String]

      value shouldBe "testText"
    }

    it("closes connection after execution") {

      val value = sqlTool.on(connectionPool)
        .query("select 'testText' as text from dual")
        .executeQuery()
        .like[String]

      connectionPool.getActiveConnections shouldBe 0
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
        .like[String]

      testValue shouldBe "test"
    }

    it("can extract predef class with like-extractor") {

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

    it("can extract list of predef classes with likeList-extractor") {

      val persons = sqlTool.on(connectionPool)
        .query(
          """
            |select 'John' as name from dual
            |union
            |select 'Jane' as name from dual
          """.stripMargin)
        .executeQuery()
        .likeList[String]

      persons shouldBe List("John", "Jane")
    }

    it("can extract list of case classes with likeList-extractor") {

      val persons = sqlTool.on(connectionPool)
        .query(
          """
            |select 'John' as name from dual
            |union
            |select 'Jane' as name from dual
          """.stripMargin)
        .executeQuery()
        .likeList[CasePerson]

      persons shouldBe List(CasePerson("John"), CasePerson("Jane"))
    }

    /*    ignore("inserts and selects clob") {

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
        .like[String]

      bookText shouldBe "Some text."
    }*/

    it("performs sanity check") {

      val books = List(
        Book(1, "Hear the Wind Sing", 1979),
        Book(2, "The catcher in the rye", 1951),
        Book(3, "War and Peace", 1869)
      )

      val executor = sqlTool.on(connectionPool)

      executor
        .query("CREATE TABLE book(id NUMBER(8) NOT NULL, title VARCHAR2(200) NOT NULL, year NUMBER(4))")
        .executeUpdate()

      books.foreach { book =>
        executor
          .query("INSERT INTO book(id, title, year) VALUES (<<id>>, <<title>>, <<year>>)")
          .params(
            "id" -> book.id,
            "title" -> book.title,
            "year" -> book.year
          )
          .executeUpdate()
          .verifyUpdates()
      }

      val modernBooks = executor
        .query("SELECT year, title, id FROM book WHERE year > <<year>> ORDER BY year")
        .params("year" -> 1900)
        .executeQuery()
        .likeList[Book]

      modernBooks shouldBe List(
        Book(2, "The catcher in the rye", 1951),
        Book(1, "Hear the Wind Sing", 1979)
      )
    }
  }
}




























