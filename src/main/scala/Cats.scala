
object Cats {

  trait Printable[A] {
    def format(a: A): String
  }

  object PrintableInstances {

    implicit val intPrintable = new Printable[Int] {

      override def format(a: Int) = a.toString
    }

    implicit val stringPrintable = new Printable[String] {

      override def format(a: String) = a
    }

    implicit val catPrintable = new Printable[Cat] {

      override def format(a: Cat) = {
        val name = Printable.format(a.name)
        val age = Printable.format(a.age)
        val color = Printable.format(a.color)
        s"${name} is a ${age} year-old ${color} cat."
      }
    }
  }

  object Printable {

    def format[A](a: A)(implicit p: Printable[A]): String = {
      p.format(a)
    }

    def print[A](a: A)(implicit p: Printable[A]): Unit = {
      println(format(a))
    }
  }

  final case class Cat(name: String, age: Int, color: String)

  object PrintableSyntax {

    implicit class PrintableOpts[A](a: A) {

      def format(implicit p: Printable[A]): String = {
        p.format(a)
      }

      def print(implicit p: Printable[A]): Unit = {
        println(format(p))
      }
    }
  }

  def main(args: Array[String]): Unit = {

    import PrintableInstances._
    import PrintableSyntax._

    val amy = Cat("Amy", 1, "white")

    "test".print
    123.print
    amy.print

    import cats._
    import cats.implicits._

    val cat1 = Cat("Garfield", 38, "orange and black")
    val cat2 = Cat("Heathcliff", 33, "orange and black")
    val optionCat1 = Option(cat1)
    val optionCat2 = Option.empty[Cat]

    println(optionCat1 === optionCat2)
  }
}
