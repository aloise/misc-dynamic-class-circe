package model.recoursive

import matryoshka.data.Fix

sealed trait Entity[V]
sealed trait Field[K,V]{ def key:K }


case class StringField[K,V](key:K) extends Field[K,V] with Entity[V]
case class IntField[K,V](key:K) extends Field[K,V] with Entity[V]
case class BooleanField[K,V](key:K) extends Field[K,V] with Entity[V]



class User {

  type ConcreteEntity = Fix[Entity]
}
