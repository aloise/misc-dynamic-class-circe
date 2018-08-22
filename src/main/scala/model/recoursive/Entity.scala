package model.recoursive

import matryoshka.data.Fix
import matryoshka.implicits._
import scalaz._
import Scalaz._
import cats.data.Nested
import io.circe.Json
import scala.reflect.runtime.universe._

sealed trait Entity[V]
sealed trait Field[K,V] extends Entity[V]{ def key:K }

case class StringField[K,V](key:K) extends Field[K,V]
case class IntField[K,V](key:K) extends Field[K,V]
case class BooleanField[K,V](key:K) extends Field[K,V]

sealed trait RecordSetAbstract[K,V] extends Entity[V] { def fields:Map[K,V] }
case class RootRecordSet[K,V](fields: Map[K,V]) extends RecordSetAbstract[K,V]
case class NestedRecordSet[K,V](key: K, fields: Map[K,V]) extends RecordSetAbstract[K,V] with Field[K,V]


object Entity {
  type Record = Fix[Entity]

  implicit def recordTraverse: Traverse[Entity] = new Traverse[Entity] {
    override def traverseImpl[G[_], A, B](fa: Entity[A])(f: A => G[B])(implicit evidence$1: Applicative[G]): G[Entity[B]] = fa match {
      case StringField(key) => Applicative[G].point(StringField(key))
      case IntField(key) => Applicative[G].point(IntField(key))
      case BooleanField(key) => Applicative[G].point(BooleanField(key))
      case RootRecordSet(fields) => fields.traverse(x => f(x)).map(RootRecordSet(_))
      case NestedRecordSet(key, fields) => fields.traverse(x => f(x)).map(NestedRecordSet(key, _))
    }
  }

  object Record {

    import io.circe.{Encoder, Json}

    trait ToCirceKey[A] {
      def toJsonObjectKey(value:A):String
    }

    implicit val stringToCirceKey: ToCirceKey[String] = (value: String) => value

    implicit def recordCirceEncoder[K : ToCirceKey : TypeTag, V : Encoder: TypeTag]:Encoder[Record] = (a: Record) => a.cata[Json] {
      case nested: NestedRecordSet[K, V] =>
        toCirceObj(nested.key)(encodeCirceNestedFieldSet[K, V](nested))
      case root: RootRecordSet[K, V] =>
        encodeCirceNestedFieldSet[K, V](root)
      case str:StringField[K,V] =>
        toCirceObj(str.key)(Json.fromString(""))
      case int:IntField[K,V] =>
        toCirceObj(int.key)(Json.fromInt(0))
      case bool:BooleanField[K,V] =>
        toCirceObj(bool.key)(Json.fromBoolean(true))
    }

    private def toCirceObj[K: ToCirceKey](key:K)(value:Json) =
      Json.obj( implicitly[ToCirceKey[K]].toJsonObjectKey(key) -> value)

    private def encodeCirceNestedFieldSet[K: ToCirceKey, V: Encoder](root: RecordSetAbstract[K,V]) = {
      Json.obj(root.fields.map { case (k, v) =>
        implicitly[ToCirceKey[K]].toJsonObjectKey(k) -> implicitly[Encoder[V]].apply(v)
      }.toSeq: _*)
    }
  }


  private def embed(m:Entity[Record]):Record = new CorecursiveOps[Record, Entity, Entity](m).embed

  def stringField[K](key:K): (K, Record) = (key, embed(StringField[K,Record](key)))
  def intField[K](key:K): (K, Record) = (key, embed(IntField[K, Record](key)))
  def booleanField[K](key:K): (K, Record) = (key, embed(BooleanField[K, Record](key)))

  def record[K](fields:(K,Record)*):Record = embed(RootRecordSet(fields.toMap))
  def nestedRecord[K,V](key:K)(fields:(K,Record)*):(K,Record) = (key, embed(NestedRecordSet(key, fields.toMap)))
}
