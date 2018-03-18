package com.ilyamur.cappuccino.todoapp

import org.scalatest.{FunSpec, Matchers}
import org.scalatest.mockito.MockitoSugar

class TodoBoardTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("The TodoBoard") {

    it("follows initial condition") {

      val board = new TodoBoard()

      board.getAll shouldBe (List())
      board.getActive shouldBe (List())
      board.getCompleted shouldBe (List())
    }

    it("adds new item") {

      val board = new TodoBoard()
      val item = board.addTodo("text")

      board.getAll shouldBe (List(item))
      board.getActive shouldBe (List(item))
      board.getCompleted shouldBe (List())
    }

    it("sets an item as completed") {

      val board = new TodoBoard()
      val item = board.addTodo("text")
      board.markCompleted(item.id)

      board.getAll shouldBe (List(item))
      board.getActive shouldBe (List())
      board.getCompleted shouldBe (List(item))
    }

    it("sets an item back as active") {

      val board = new TodoBoard()
      val item = board.addTodo("text")
      board.markCompleted(item.id)
      board.markActive(item.id)

      board.getAll shouldBe (List(item))
      board.getActive shouldBe (List(item))
      board.getCompleted shouldBe (List())
    }

    it("clear completed items") {

      val board = new TodoBoard()
      val item = board.addTodo("text")
      board.markCompleted(item.id)
      board.clearCompleted()

      board.getAll shouldBe (List())
      board.getActive shouldBe (List())
      board.getCompleted shouldBe (List())
    }

    it("satisfies sanity check") {

      val board = new TodoBoard()

      val itemA = board.addTodo("itemA")
      val itemB = board.addTodo("itemB")

      board.getAll shouldBe (List(itemA, itemB))
      board.getActive shouldBe (List(itemA, itemB))
      board.getCompleted shouldBe (List())
      
      board.markCompleted(itemA.id)

      board.getAll shouldBe (List(itemA, itemB))
      board.getActive shouldBe (List(itemB))
      board.getCompleted shouldBe (List(itemA))

      board.clearCompleted()

      board.getAll shouldBe (List(itemB))
      board.getActive shouldBe (List(itemB))
      board.getCompleted shouldBe (List())
    }
  }
}
