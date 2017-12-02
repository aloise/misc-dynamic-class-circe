package model.codecs

import RefinedTests.HexChar
import eu.timepit.refined.api.{RefType, Refined}
import eu.timepit.refined.char._
import eu.timepit.refined.collection._
import eu.timepit.refined.generic._
import eu.timepit.refined.numeric._
import eu.timepit.refined.collection._
import shapeless.nat._
import eu.timepit.refined.W
import shapeless.ops._
import shapeless.syntax.sized._
import shapeless.{Nat, Sized, _}
import shapeless.ops.nat._
import shapeless.Sized._
import eu.timepit.refined.boolean.Or
import shapeless.ops.sized._
import shapeless.ops.hlist._
import syntax.std.traversable._
import syntax.singleton._
import syntax.typeable._
import ops.hlist._

import scala.collection.generic.{CanBuildFrom, IsTraversableLike}
import scala.collection._
import eu.timepit.refined.auto._

object RefinedTests {

  type HexChar = Char Refined Or[Digit,Interval.Closed[ W.`'A'`.T, W.`'F'`.T]]
  type ListHexString[N <: Nat] = Sized[ List[HexChar], N ]


  object SizedSplitByN {

    def groupByN[N <: Nat, DIV <: Nat, A, CC[+A] <: IterableLike[A, CC[A]], OUTN <: (N Div DIV)#Out ]
    ( splitByN: DIV )
    ( x:Sized[CC[A],N] )
    (implicit tr: IsTraversableLike[CC[A]],
     toHList: ToHList[CC[A],N],
     additive0: shapeless.AdditiveCollection[CC[Sized[CC[A], splitByN.N]]],
     additive1: shapeless.AdditiveCollection[CC[A]],
     toInt: ToInt[DIV],
     modAux: Mod.Aux[N, DIV, _0],
     bf: CanBuildFrom[CC[A], Sized[CC[A], splitByN.N], CC[Sized[CC[A], splitByN.N]]]
    ): Sized[CC[Sized[CC[A], splitByN.N]], OUTN] = {

      val groupedIterator: Iterator[Sized[CC[A], splitByN.N]] = x.unsized.grouped(toInt()).map(grouped => Sized.wrap[CC[A],splitByN.N](grouped))
      val builder = bf.apply(x)

      groupedIterator.foreach{ i =>
        builder += i
      }


      Sized.wrap[CC[Sized[CC[A], splitByN.N]], OUTN](builder.result())
    }
  }




  def test(): Unit = {

    val unsizedList: List[HexChar] = List('A','B','1','F').flatMap(f => RefType.applyRef[HexChar](f).toOption )

    val sizedList = Sized.wrap[List[HexChar],_4](unsizedList)

    println( SizedSplitByN.groupByN(_2)(sizedList))

    // val items:NaturalList = RefType.applyRef[NaturalList]( List(1,2,3) )


  }

}
