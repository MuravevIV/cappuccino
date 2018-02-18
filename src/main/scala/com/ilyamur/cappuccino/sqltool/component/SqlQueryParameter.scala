package com.ilyamur.cappuccino.sqltool.component

object SqlQueryParameter {

  def from(pair: (String, Any)): SqlQueryParameter = {
    new SqlQueryParameter(pair._1, pair._2)
  }
}

case class SqlQueryParameter(name: String, value: Any)
