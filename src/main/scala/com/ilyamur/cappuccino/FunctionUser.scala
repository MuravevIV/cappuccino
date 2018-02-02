package com.ilyamur.cappuccino

class FunctionUser(val functionProvider: FunctionProvider) {

  def do1And1: Int = functionProvider.addingFunction(1)(1)

  def batman: Seq[String] = functionProvider.textNTimesFunction(8, "na") :+ "batman"

  def answer: Int = {
    val addToNineteenFunction: (Int => Int) = functionProvider.sumCurried(19)
    functionProvider.applies23ToFunction(addToNineteenFunction)
  }

  def ballmer: String = {
    val textNTimesAsStringCurried = functionProvider.textNTimesAsXCurried { seq =>
      seq.mkString(", ")
    }
    val text4TimesAsString = textNTimesAsStringCurried(4)
    text4TimesAsString("developers")
  }
}
