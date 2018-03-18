package com.ilyamur.cappuccino.todoapp

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class TodoItemTest extends FunSpec
  with Matchers
  with MockitoSugar {

  describe("The TodoItem") {

    it("follows initial condition") {

      val item = new TodoItem(1, "text")

      item.id shouldBe (1)
      item.text shouldBe ("text")
      item.isActive shouldBe (true)
      item.isCompleted shouldBe (false)
    }

    it("marks as completed") {

      val item = new TodoItem(1, "text")

      item.markCompleted()

      item.id shouldBe (1)
      item.text shouldBe ("text")
      item.isActive shouldBe (false)
      item.isCompleted shouldBe (true)
    }

    it("marks back as active") {

      val item = new TodoItem(1, "text")

      item.markCompleted()
      item.markActive()

      item.id shouldBe (1)
      item.text shouldBe ("text")
      item.isActive shouldBe (true)
      item.isCompleted shouldBe (false)
    }
  }
}
