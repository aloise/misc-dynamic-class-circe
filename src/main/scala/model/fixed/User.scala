package model.fixed


import model.fixed.Entity._
import model.fixed._
import matryoshka._
import matryoshka.implicits._
import scalaz.Scalaz._

object User {
  val model:Record = record(
    "id" -> intField(1),
    "username" -> stringField("UUUUDD"),
    "isFunny" -> booleanField(true),
    "features" -> record(
      "isBusy" -> booleanField(true),
      "age" -> intField(22),
      "weight" -> intField(95)
    )
  )

  import io.circe.syntax._
  import io.circe.Json
  import io.circe.generic.auto._
  import Record._

  val json:Json = model.asJson

}
