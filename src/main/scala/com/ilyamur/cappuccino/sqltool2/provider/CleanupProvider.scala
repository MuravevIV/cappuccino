package com.ilyamur.cappuccino.sqltool2.provider

import java.util.concurrent.atomic.AtomicReference

abstract class CleanupProvider[T](func: (() => T)) extends (() => T) {

  protected val refValue: AtomicReference[T] = new AtomicReference[T]()

  override def apply(): T = synchronized {
    if (refValue.get() == null) {
      val value = func()
      refValue.set(value)
      value
    } else {
      throw new IllegalStateException("Can only call provider get method once")
    }
  }

  def cleanup(): Unit
}
