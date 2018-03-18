package com.ilyamur.cappuccino.todoapp

import scala.collection.mutable.ListBuffer

class TodoBoard {

  private var counter = 0
  private val listBuffer = new ListBuffer[TodoItem]()

  def addTodo(text: String): TodoItem = {
    val item = new TodoItem(0, text)
    counter += 1
    listBuffer += item
    item
  }

  def markCompleted(id: Long) = {
    listBuffer
      .find(_.id == id)
      .foreach(_.markCompleted())
  }

  def markActive(id: Long): Unit = {
    listBuffer
      .find(_.id == id)
      .foreach(_.markActive())
  }

  def getAll: List[TodoItem] = listBuffer.toList

  def getActive: List[TodoItem] = listBuffer.filter(_.isActive).toList

  def getCompleted: List[TodoItem] = listBuffer.filter(_.isCompleted).toList

  def clearCompleted(): Unit = {
    listBuffer --= getCompleted
  }
}
