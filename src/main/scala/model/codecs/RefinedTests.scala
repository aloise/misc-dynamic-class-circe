package model.codecs

import eu.timepit.refined.api.{RefType, Refined}
import eu.timepit.refined.boolean._
import eu.timepit.refined.char._
import eu.timepit.refined.collection._
import eu.timepit.refined.generic._
import eu.timepit.refined.numeric.{Divisible, Even, Interval, Positive}
import eu.timepit.refined.string._
import eu.timepit.refined.collection._
import shapeless.nat._
import eu.timepit.refined.W
import shapeless.ops._
import shapeless.syntax.sized._
import shapeless._
import shapeless.ops.nat._

import scala.collection.generic.{CanBuildFrom, IsTraversableLike}
import scala.collection._

object RefinedTests {

  import eu.timepit.refined.auto._

  type HexChar = Char Refined Or[Digit,Interval.Closed[ W.`'A'`.T, W.`'F'`.T]]
  type ListHexString[N <: Nat] = Sized[ List[HexChar], N ]

  type NaturalInt = Int Refined Positive

  // def hexStringToBytes(hexString: HexString):Int = ???

  /*
  def split2[ INR, XX[ M <: Traversable[INR], RF <: Size[Even]], RES ]( items: XX[_,_] )(
    implicit // mf: CanBuildFrom[M, (INR,INR), RES],
    refType: RefType[XX]
  ) :RES = {
    ???
  }
  */

  trait SizedSplitByN {
    type Out
  }

  object SizedSplitByN {

    def sizedSplitByN[DIV <: Nat, N <: Nat, DELTA <: Nat, A, CC[+X] <: GenTraversable[X], B <: Sized[CC[A],DIV], OUTN <: (N Div DIV)#Out, MODN <: N Mod DIV]
      (splitByN: DIV)(x: Sized[CC[A], N])
      (implicit bf: CanBuildFrom[CC[A], B, CC[B]],
       tr: IsTraversableLike[CC[A]],
       additiveA: AdditiveCollection[CC[A]],
       additiveB: AdditiveCollection[CC[B]],
       modIsZero: Mod.Aux[N, DIV, _0],
       diff: Diff.Aux[N, splitByN.N, DELTA],
       splitByNToInt: ToInt[splitByN.N]
      ): Sized[CC[B], OUTN] = {

        val outElem0 = x.take(splitByN).asInstanceOf[B]
        val teil0 = x.drop(splitByN)
        val builder = bf.apply(x)

        builder += outElem0

        Sized.wrap[CC[B],OUTN](builder.result)

        ???

      }
  }

  def test(): Unit = {


    val unsizedList: List[HexChar] = List('A','B','1','F', 'L').flatMap(f => RefType.applyRef[HexChar](f).toOption )

    val sizedList = Sized.wrap[List[HexChar],_4](unsizedList)

    println(SizedSplitByN.sizedSplitByN(_2)(sizedList))

    // val items:NaturalList = RefType.applyRef[NaturalList]( List(1,2,3) )


  }

}
