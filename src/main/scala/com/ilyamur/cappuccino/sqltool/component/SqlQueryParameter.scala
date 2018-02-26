package com.ilyamur.cappuccino.sqltool.component

case class SqlQueryParameter(name: String, value: Any) {

  def this(pair: (String, Any)) = {
    this(pair._1, pair._2)
  }
}
