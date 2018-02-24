package com.ilyamur.cappuccino.sqltool.component

import java.sql.{ResultSet, ResultSetMetaData}

import com.ilyamur.cappuccino.sqltool.SqlTool
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class SqlQueryRowTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("the SqlQueryRow") {

    it("can extract all basic types") {

      val resultSet = mock[ResultSet]
      val resultSetMetaData = mock[ResultSetMetaData]
      when(resultSet.getMetaData).thenReturn(resultSetMetaData)
      when(resultSetMetaData.getColumnCount).thenReturn(9)
      when(resultSet.getObject(1)).thenReturn(false.asInstanceOf[Object], null)
      when(resultSet.getObject(2)).thenReturn(0.toByte.asInstanceOf[Object], null)
      when(resultSet.getObject(3)).thenReturn(0.toShort.asInstanceOf[Object], null)
      when(resultSet.getObject(4)).thenReturn(0.asInstanceOf[Object], null)
      when(resultSet.getObject(5)).thenReturn(0.toLong.asInstanceOf[Object], null)
      when(resultSet.getObject(6)).thenReturn(0.toFloat.asInstanceOf[Object], null)
      when(resultSet.getObject(7)).thenReturn(0.toDouble.asInstanceOf[Object], null)
      when(resultSet.getObject(8)).thenReturn('a'.asInstanceOf[Object], null)
      when(resultSet.getObject(9)).thenReturn("abc".asInstanceOf[Object], null)

      val queryRow = SqlQueryRow.from(resultSet, SqlTool.Context())

      // todo
      /*
            val booleanValue: Boolean = queryRow.likebooleanTyped, 1)
            val byteValue: Short = queryRow.asTyped(byteTyped, 2)
            val shortValue: Short = queryRow.asTyped(shortTyped, 3)
            val intValue: Int = queryRow.asTyped(intTyped, 4)
            val longValue: Long = queryRow.asTyped(longTyped, 5)
            val floatValue: Float = queryRow.asTyped(floatTyped, 6)
            val doubleValue: Double = queryRow.asTyped(doubleTyped, 7)
            val charValue: Char = queryRow.asTyped(charTyped, 8)
            val stringValue: String = queryRow.asTyped(stringTyped, 9)*/
    }
  }
}




























