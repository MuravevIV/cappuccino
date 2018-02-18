package com.ilyamur.cappuccino.sqltool

import com.ilyamur.cappuccino.sqltool.SqlTypes._
import com.ilyamur.cappuccino.sqltool.component.{SqlEntity, SqlQueryRow}
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
        .asSingle[Person]

      val johnPerson = new Person()
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
        .asListOf[Person]

      val johnPerson = new Person()
      johnPerson.name = "John"
      val janePerson = new Person()
      janePerson.name = "Jane"

      person shouldBe List(johnPerson, janePerson)
    }

    it("can execute simple DDL") {

      val updateResult = sqlTool.on(connectionPool)
        .query("create table t (f varchar2)")
        .executeUpdate()

      updateResult
    }

    it("") {

      val updateResult = sqlTool.on(connectionPool)
        .query("insert into person (name) values ('John')")
        .executeUpdate()

      updateResult
    }
  }
}

class Person() extends SqlEntity[Person] {

  var name: String = _

  override def fillOn(queryRow: SqlQueryRow): Unit = {
    this.name = queryRow.asTyped(stringTyped, 1)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Person]

  override def equals(other: Any): Boolean = other match {
    case that: Person =>
      (that canEqual this) &&
        name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}



























