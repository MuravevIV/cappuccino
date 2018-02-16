package com.ilyamur.cappuccino.sqltool2.provider

class ClosingProvider[T <: AutoCloseable](func: (() => T)) extends CleanupProvider[T](func) {

  override def cleanup(): Unit = {
    val value = refValue.get()
    if (value != null) {
      value.close()
    }
  }
}
