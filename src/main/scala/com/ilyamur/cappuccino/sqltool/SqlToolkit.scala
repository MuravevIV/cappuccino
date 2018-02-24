package com.ilyamur.cappuccino.sqltool

object SqlToolkit {

  def getDefaultTool: SqlTool = {

    val sqlTool = new SqlTool()

    sqlTool.registerPostQueryTransformer((o: Boolean) => o)
    sqlTool.registerPostQueryTransformer((o: Byte) => o)
    sqlTool.registerPostQueryTransformer((o: Short) => o)
    sqlTool.registerPostQueryTransformer((o: Int) => o)
    sqlTool.registerPostQueryTransformer((o: Long) => o)
    sqlTool.registerPostQueryTransformer((o: Float) => o)
    sqlTool.registerPostQueryTransformer((o: Double) => o)
    sqlTool.registerPostQueryTransformer((o: Char) => o)
    sqlTool.registerPostQueryTransformer((o: String) => o)

    sqlTool.registerPostQueryTransformer((o: java.math.BigDecimal) => o.intValue())
    sqlTool.registerPostQueryTransformer((o: java.math.BigDecimal) => o.longValue())

    sqlTool
  }
}
