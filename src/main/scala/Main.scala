import model.user._
import io.circe.syntax._
import model.codecs.RefinedTests
import model.recoursive.Exp

object Main extends App {

  val user = User(
    Username("124"),
    UserId(12345),
    UserIsAlive(false),
    UserAddress(UserAddressInfo("City", "State")),
    UserIsAlive(true),
    Username("New Username")
  )

  val user2 = user.delete[UserId]

  // println( user.asJson )

  // println( user2.asJson )

  // println(user2.get[UserIsAlive])

  // RefinedTests.test()


  {
    import matryoshka.implicits._

    println(Exp.exp1.cata(Exp.evaluate))
  }

}
