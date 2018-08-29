package model.recoursive

import matryoshka.data.Fix
import matryoshka.implicits._
import scalaz._
import Scalaz._
import cats.data.Nested
import io.circe.Decoder.Result
import io.circe.Json._
import io.circe._
import matryoshka.{Algebra, Coalgebra}

import scala.reflect.runtime.universe._

sealed trait Field[R]{
  type V
  def value:V
}

case class StringField[R](value:String) extends Field[R]{ type V = String}
case class IntField[R](value: Int) extends Field[R]{ type V = Int }
case class BooleanField[R](value: Boolean) extends Field[R]{ type V = Boolean}
case class RecordSet[R](value: Map[String,R]) extends Field[R]{ type V = Map[String, R]}
case class ArrayField[R](value:Vector[R]) extends Field[R]{ type V = Vector[R]}
case class EmptyField[R]() extends Field[R]{ type V = Nothing ; def value:Nothing = ??? }

object Entity {
  type Record = Fix[Field]

  implicit def recordTraverse: Traverse[Field] = new Traverse[Field] {
    override def traverseImpl[G[_], A, B](fa: Field[A])(f: A => G[B])(implicit evidence$1: Applicative[G]): G[Field[B]] = fa match {
      case StringField(v) => Applicative[G].point(StringField(v))
      case IntField(v) => Applicative[G].point(IntField(v))
      case BooleanField(v) => Applicative[G].point(BooleanField(v))
      case ArrayField(fields) => fields.traverse(x => f(x)).map(ArrayField(_))
      case RecordSet(fields) => fields.traverse(x => f(x)).map(RecordSet(_))
      case EmptyField() => Applicative[G].point(EmptyField())

    }
  }

  object Record {

    import io.circe.{Encoder, Json}

    trait ToCirceKey[A] {
      def toJsonObjectKey(value:A):String
    }

    implicit val stringToCirceKey: ToCirceKey[String] = (value: String) => value

    val algebraFieldJson : Algebra[Field, Json] = {
      case StringField(v) => Json.fromString(v)
      case IntField(v) => Json.fromInt(v)
      case BooleanField(v) => Json.fromBoolean(v)
      case RecordSet(fields) => Json.obj(fields.toList:_*)
      case ArrayField(values) => Json.arr(values:_*)
      case EmptyField() => Json.Null
    }

    val coalgebraFieldJson : Coalgebra[Field, Json] = (json:Json) => json.fold(
      EmptyField(),
      jsonBoolean => BooleanField(jsonBoolean),
      jsonNumber => IntField(jsonNumber.toInt.getOrElse(0)),
      jsonString => StringField(jsonString),
      jsonArray => ArrayField(jsonArray),
      jsonObject => RecordSet(jsonObject.toMap)
    )

    implicit def toCirceEncoder:Encoder[Record] =
      (a: Record) => a.cata(algebraFieldJson)

    implicit def toCirceDecoder:Decoder[Record] = (c: HCursor) =>
      c.as[Json].map( _.ana[Record](coalgebraFieldJson))

  }


  private def embed(m:Field[Record]):Record = new CorecursiveOps[Record, Field, Field](m).embed

  def stringField(value:String): Record = embed(StringField[Record](value))
  def intField(value:Int): Record = embed(IntField[Record](value))
  def booleanField(value:Boolean): Record = embed(BooleanField[Record](value))
  def record(fields:(String,Record)*): Record = embed(RecordSet[Record](fields.toMap))
  def array(fields:Record*): Record = embed(ArrayField[Record](fields.toVector))

}
