package com.ilyamur.cappuccino.sqltool2.provider

import java.util.concurrent.atomic.AtomicReference

abstract class CleanupProvider[T](func: (() => T)) extends (() => T) {

  protected val refValue: AtomicReference[T] = new AtomicReference[T]()

  override def apply(): T = synchronized {
    val currValue = refValue.get()
    if (currValue == null) {
      val value = func()
      refValue.set(value)
      value
    } else {
      currValue
    }
  }

  def cleanup(): Unit
}
