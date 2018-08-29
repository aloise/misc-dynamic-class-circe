package model.recoursive

import matryoshka.data.Fix
import matryoshka.implicits._
import scalaz._
import Scalaz._
import cats.data.Nested
import io.circe.{Json, JsonObject}
import matryoshka.Algebra

import scala.reflect.runtime.universe._

sealed trait Field[R]{
  type V
  def value:V
}

case class StringField[R](value:String) extends Field[R]{ type V = String}
case class IntField[R](value: Int) extends Field[R]{ type V = Int }
case class BooleanField[R](value: Boolean) extends Field[R]{ type V = Boolean}
case class RecordSet[R](value: Map[String,R]) extends Field[R]{ type V = Map[String, R]}


object Entity {
  type Record = Fix[Field]

  implicit def recordTraverse: Traverse[Field] = new Traverse[Field] {
    override def traverseImpl[G[_], A, B](fa: Field[A])(f: A => G[B])(implicit evidence$1: Applicative[G]): G[Field[B]] = fa match {
      case StringField(v) => Applicative[G].point(StringField(v))
      case IntField(v) => Applicative[G].point(IntField(v))
      case BooleanField(v) => Applicative[G].point(BooleanField(v))
      case RecordSet(fields) => fields.traverse(x => f(x)).map(RecordSet(_))
    }
  }

  object Record {

    import io.circe.{Encoder, Json}

    trait ToCirceKey[A] {
      def toJsonObjectKey(value:A):String
    }

    implicit val stringToCirceKey: ToCirceKey[String] = (value: String) => value

    val evaluateJson : Algebra[Field, Json] = {
      case StringField(v) => Json.fromString(v)
      case IntField(v) => Json.fromInt(v)
      case BooleanField(v) => Json.fromBoolean(v)
      case RecordSet(fields) => Json.obj(fields.toList:_*)
    }

    implicit def toCirceEncoder:Encoder[Record] =
      (a: Record) => a.cata(evaluateJson)

  }


  private def embed(m:Field[Record]):Record = new CorecursiveOps[Record, Field, Field](m).embed

  def stringField(value:String): Record = embed(StringField[Record](value))
  def intField(value:Int): Record = embed(IntField[Record](value))
  def booleanField(value:Boolean): Record = embed(BooleanField[Record](value))
  def record(fields:(String,Record)*): Record = embed(RecordSet[Record](fields.toMap))

}
