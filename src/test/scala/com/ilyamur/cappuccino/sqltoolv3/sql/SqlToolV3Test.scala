package com.ilyamur.cappuccino.sqltoolv3.sql

import com.ilyamur.cappuccino.{Book, H2}
import org.h2.jdbcx.JdbcConnectionPool
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}

class SqlToolV3Test extends FunSpec
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

  describe("the SqlToolV3 query functionality") {

    it("performs sanity check") {

      val module = new ESqlToolModule() {}
      val sqlTool = module.tool

      val books = List(
        Book(1, "Hear the Wind Sing", 1979),
        Book(2, "The catcher in the rye", 1951),
        Book(3, "War and Peace", 1869)
      )

      val executor = sqlTool.onDataSource(connectionPool)

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
        .asList[Book]

      modernBooks shouldBe List(
        Book(2, "The catcher in the rye", 1951),
        Book(1, "Hear the Wind Sing", 1979)
      )
    }
  }
}




























