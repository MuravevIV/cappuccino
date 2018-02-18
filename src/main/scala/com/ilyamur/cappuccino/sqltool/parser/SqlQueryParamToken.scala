package com.ilyamur.cappuccino.sqltool.parser

case class SqlQueryParamToken(name: String, props: Map[String, Any] = Map.empty) extends SqlQueryToken
