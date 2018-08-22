package model.recoursive

import io.circe.{Encoder, Json}

object Circe {

  implicit def toCirceEncoder[Record]:Encoder[Record] = new Encoder[Record] {
    override def apply(a: Record): Json = ???
  }

}
