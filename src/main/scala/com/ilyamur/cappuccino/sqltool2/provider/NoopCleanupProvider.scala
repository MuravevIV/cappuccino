package com.ilyamur.cappuccino.sqltool2.provider

class NoopCleanupProvider[T <: AutoCloseable](func: (() => T)) extends CleanupProvider[T](func) {

  override def cleanup(): Unit = {
    // noop
  }
}
