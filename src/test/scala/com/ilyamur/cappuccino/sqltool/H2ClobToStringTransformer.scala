package com.ilyamur.cappuccino.sqltool

import org.h2.jdbc.JdbcClob
import org.h2.util.IOUtils

object H2ClobToStringTransformer extends (JdbcClob => String) {

  override def apply(jdbcClob: JdbcClob): String = {
    IOUtils.readStringAndClose(jdbcClob.getCharacterStream, -1)
  }
}
