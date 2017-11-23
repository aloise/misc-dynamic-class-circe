package model.user

abstract class FieldName {
  type V
}


import io.circe._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}
import shapeless._
import shapeless.labelled.FieldType

import scala.reflect.ClassTag


trait FieldData extends FieldName {
  def data: V
}

abstract class Entity[F <: FieldData](initWithFields: F*)(implicit labelledGeneric: LabelledGeneric[F]) {

  type UnderlyingMap = Map[Class[F], F]

  protected val underlying: UnderlyingMap = initWithFields.foldLeft[UnderlyingMap](Map.empty) { case (map, f) =>
    map.updated(f.getClass.asInstanceOf[Class[F]], f)
  }
}


sealed trait UserField extends FieldData

case class Username(data: String) extends UserField {
  type V = String
}

case class Password(data: String) extends UserField {
  type V = String
}

case class UserId(data: Int) extends UserField {
  type V = Int
}

case class UserIsAlive(data: Boolean) extends UserField {
  type V = Boolean
}

case class UserAddressInfo(city: String, state: String)

case class UserAddress(data: UserAddressInfo) extends UserField {
  type V = UserAddressInfo
}

object UserField {

  import io.circe.generic.auto._

  protected[user] final val typeNameField = "$type"
  protected[user] final val dataNameField = "data"

  private val gen = LabelledGeneric[UserField]

  implicit def circeEncoder: ObjectEncoder[UserField] = (a: UserField) => {
    gen.to(a).asJsonObject(cconsJsonWrites)
  }

  // this exception would not appear in practice but it's required to summon an implicit
  private implicit val encodeCNil: ObjectEncoder[CNil] =
    (a: CNil) => throw new IllegalArgumentException("Cannot encode CNil")

  private implicit def cconsJsonWrites[Key <: Symbol, Head <: UserField, Tail <: Coproduct](
                                                                                             implicit key: Witness.Aux[Key],
                                                                                             headEncoder: Lazy[ObjectEncoder[Head]],
                                                                                             tailEncoder: Lazy[ObjectEncoder[Tail]]
                                                                                           ): ObjectEncoder[FieldType[Key, Head] :+: Tail] =
    ObjectEncoder[FieldType[Key, Head] :+: Tail] {
      _.eliminate(
        { head =>
          val jsonObj = headEncoder.value.encodeObject(head)
          jsonObj.add(typeNameField, Json.fromString(key.value.name))

        }, { tail =>
          tailEncoder.value.encodeObject(tail)
        }
      )
    }


}

case class User(fields: UserField*) extends Entity[UserField](fields: _*) {

  def put(field: UserField): User =
    new User(fields :+ field: _*)

  def delete[T <: UserField]()(implicit classTag: ClassTag[T]): User =
    new User(fields.filterNot(f => f.getClass == classTag.runtimeClass): _*)


}

object User {

  implicit val gen = LabelledGeneric[UserField]

  implicit def userCirceEncoder: Encoder[User] = (a: User) => {
    val pairs =
      a.underlying.flatMap { case (_, f) =>
        val finalJson = f.asJsonObject(UserField.circeEncoder)

        for {
          typeNameJson <- finalJson.apply(UserField.typeNameField)
          typeNameStr <- typeNameJson.asString
          enclosedData <- finalJson.apply(UserField.dataNameField)
        } yield (typeNameStr, enclosedData.asJson)


      }

    Json.obj(pairs.toSeq: _*)

  }


}