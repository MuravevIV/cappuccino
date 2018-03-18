package com.ilyamur.cappuccino.todoapp

object TodoappLighthouse {

  val todoBoard = new TodoBoard()
  val todoItem = todoBoard.addTodo("text")
  todoBoard.markCompleted(todoItem.id)
  val allTodoItems: List[TodoItem] = todoBoard.getAll
  val activeTodoItems: List[TodoItem] = todoBoard.getActive
  val completedTodoItems: List[TodoItem] = todoBoard.getCompleted
  todoBoard.clearCompleted()

  todoItem.markCompleted()
  todoItem.markActive()
}
