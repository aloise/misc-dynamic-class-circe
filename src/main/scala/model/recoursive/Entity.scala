package model.recoursive

import matryoshka.data.Fix
import matryoshka.implicits._
import scalaz._
import Scalaz._

sealed trait Entity[V]
sealed trait Field[K,V]{ def key:K }

case class StringField[K,V](key:K) extends Field[K,V] with Entity[V]
case class IntField[K,V](key:K) extends Field[K,V] with Entity[V]
case class BooleanField[K,V](key:K) extends Field[K,V] with Entity[V]

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

  private def embed(m:Entity[Record]):Record = new CorecursiveOps[Record, Entity, Entity](m).embed

  def stringField[K](key:K): (K, Record) = (key, embed(StringField[K,Record](key)))
  def intField[K](key:K): (K, Record) = (key, embed(IntField[K, Record](key)))
  def booleanField[K](key:K): (K, Record) = (key, embed(BooleanField[K, Record](key)))

  def record[K](fields:(K,Record)*):Record = embed(RootRecordSet(fields.toMap))
  def nestedRecord[K,V](key:K)(fields:(K,Record)*):(K,Record) = (key, embed(NestedRecordSet(key, fields.toMap)))
}
