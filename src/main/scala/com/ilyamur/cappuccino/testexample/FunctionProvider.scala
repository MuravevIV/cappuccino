package com.ilyamur.cappuccino.testexample

class FunctionProvider {

  def addingFunction(addent: Int): (Int => Int) = {
    (n: Int) => n + addent
  }

  def textNTimesFunction: (Int, String) => Seq[String] = {
    (times, text) => (1 to times).map(_ => text)
  }

  def applies23ToFunction(intFunction: Int => Int): Int = {
    intFunction(23)
  }

  def sumCurried(addent1: Int)(addent2: Int): Int = {
    addent1 + addent2
  }

  def textNTimesAsXCurried[T](stringSeqTransformer: (Seq[String] => T)): (Int => String => T) = {
    times => text => stringSeqTransformer(textNTimesFunction(times, text))
  }
}
