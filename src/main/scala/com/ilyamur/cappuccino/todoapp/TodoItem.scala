package com.ilyamur.cappuccino.todoapp

class TodoItem(val id: Long, val text: String) {

  private var _isActive: Boolean = true

  def isActive: Boolean = _isActive

  def isCompleted: Boolean = !isActive

  def markCompleted(): Unit = {
    _isActive = false
  }

  def markActive() = {
    _isActive = true
  }
}

