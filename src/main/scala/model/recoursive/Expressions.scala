package model.recoursive

import io.circe.Json
import matryoshka.Algebra
import matryoshka.data.Fix
import matryoshka.implicits._
import model.recoursive
import scalaz.Functor

sealed trait Exp[A]

final case class IntValue[A](v: Int) extends Exp[A]

final case class Sum[A](exp1: A, exp2: A) extends Exp[A]

final case class Multiply[A](exp1: A, exp2: A) extends Exp[A]

final case class Divide[A](exp1: A, exp2: A) extends Exp[A]

final case class Square[A](exp: A) extends Exp[A]


object Exp {

  implicit val functor: Functor[Exp] = new Functor[Exp] {
    def map[A, B](exp: Exp[A])(f: A => B):
    Exp[B] = exp match {
      case Sum(a1, a2) =>
        Sum(f(a1),f(a2))
      case Multiply(a1, a2) =>
        Multiply(f(a1),f(a2))
      case Divide(a1, a2) =>
        Divide(f(a1), f(a2))
      case Square(a) =>
        Square(f(a))
      case IntValue(v) =>
        IntValue(v)
    }
  }

  // Exp[Double] => Double

  val evaluate : Algebra[Exp, Int] = {
    case IntValue(v) => v
    case Sum(a1, a2) => a1 + a2
    case Multiply(a1, a2) => a1 * a2
    case Square(a) => a * a
    case Divide(a1, a2) => a1 / a2
  }

  val exp1: Fix[Exp] = Fix(Sum(Fix(IntValue(5)),Fix(Square(Fix(IntValue(5))))))

}