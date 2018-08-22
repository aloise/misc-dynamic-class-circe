package model.recoursive


import model.recoursive.Entity._
import model.recoursive._
import matryoshka._
import matryoshka.implicits._
import scalaz.Scalaz._

object User {
  val model:Record = record[String](
    intField("id"),
    stringField("username"),
    booleanField("isFunny"),
    nestedRecord("features")(
      booleanField("isBusy"),
      intField("age"),
      intField("weight")
    )
  )

  import io.circe.syntax._
  import io.circe.Json
  import io.circe.generic.auto._
  import Record._

  val json:Json = model.asJson
}
