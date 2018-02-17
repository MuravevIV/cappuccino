package com.ilyamur.cappuccino.sqltool

import java.io.File

import com.ilyamur.cappuccino.sqltool.SqlTypes._
import com.ilyamur.cappuccino.sqltool.component.{SqlEntity, SqlQueryRow}
import org.h2.jdbcx.JdbcConnectionPool
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}

class SqlToolTest extends FunSpec
  with Matchers
  with MockitoSugar
  with BeforeAndAfterAll {

  Class.forName("org.h2.Driver")

  val H2_FILE_LOCATION = "target/h2.mv.db"
  val CONNECTION_URL = "jdbc:h2:./target/h2"
  val CONNECTION_USER = "sa"
  val CONNECTION_PASSWORD = ""

  var dataSource: JdbcConnectionPool = _

  override def beforeAll(): Unit = {
    super.beforeAll()

    new File(H2_FILE_LOCATION).delete()
    dataSource = JdbcConnectionPool.create(CONNECTION_URL, CONNECTION_USER, CONNECTION_PASSWORD)
  }

  override def afterAll(): Unit = {
    dataSource.dispose()

    super.afterAll()
  }

  describe("the SqlTool query functionality") {

    val sqlTool = new SqlTool()

    it("executes plain query and extracts primitive result") {

      val value = sqlTool.on(dataSource)
        .query("select 'testText' as text from dual")
        .executeQuery()
        .asSingleTyped(stringTyped)

      value shouldBe "testText"
    }

    it("closes connection after execution") {

      val value = sqlTool.on(dataSource)
        .query("select 'testText' as text from dual")
        .executeQuery()
        .asSingleTyped(stringTyped)

      dataSource.getActiveConnections shouldBe 0
    }

    it("can extract list of primitive results") {

      val value = sqlTool.on(dataSource)
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

      val person = sqlTool.on(dataSource)
        .query("select 'John' as name from dual")
        .executeQuery()
        .asSingle[Person]

      val johnPerson = new Person()
      johnPerson.name = "John"

      person shouldBe johnPerson
    }

    it("can extract list of sql entity objects") {

      val person = sqlTool.on(dataSource)
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

      val updateResult = sqlTool.on(dataSource)
        .query("create table person (name varchar2)")
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



























