package ShowExample

import scala.compiletime.*
import scala.compiletime.ops.int.*
//import scala.compiletime.ops.int.=
import scala.Tuple.*
import scala.compiletime.ops.any.==
import scala.compiletime.ops.boolean.*
import scala.deriving.Mirror

object Main2 {

  trait TupleForeachBase {
    inline def iteration[E](inline elem: E): Unit

    inline def run[T <: Tuple](inline tuple: T): Unit = rec[T](0)(tuple)

    inline def rec[T <: Tuple](inline index: Int)(inline tuple: T): Unit = {
      inline tuple match {
        case EmptyTuple => ()
        case _: NonEmptyTuple if constValue[index.type >= Tuple.Size[T]] => ()
        case n: NonEmptyTuple =>
          iteration(n(index))
          rec[T](index + 1)(tuple)
      }
    }
  }

  trait TupleFoldBase[A] {
    inline def iteration[E](inline acc: A, inline elem: E): A

    inline def run[T <: Tuple](inline acc: A, tuple: T): A = rec[T](acc, 0)(tuple)

    inline def rec[T <: Tuple](inline acc: A, index: Int)(inline tuple: T): A =
      inline tuple match {
        case EmptyTuple => acc
        case _: NonEmptyTuple if constValue[index.type >= Tuple.Size[T]] => acc
        case n: NonEmptyTuple =>
          rec[T](iteration(acc, n(index)), index + 1)(tuple)
      }
  }

  trait TupleFoldIndexedBase[A] {
    inline def iteration[E](inline acc: A, inline elem: E, index: Int): A

    inline def run[T <: Tuple](inline acc: A, tuple: T): A = rec[T](acc, 0)(tuple)

    inline def rec[T <: Tuple](inline acc: A, index: Int)(inline tuple: T): A =
      inline tuple match {
        case EmptyTuple => acc
        case _: NonEmptyTuple if constValue[index.type >= Tuple.Size[T]] => acc
        case n: NonEmptyTuple =>
          rec[T](iteration(acc, n(index), index), index + 1)(tuple)
      }
  }


  trait CaseClassDeriveBase[A] {
    inline def start(inline acc: A)(name: String): A

    inline def end(inline acc: A): A

    inline def element[E](inline acc: A)(inline elem: E, inline label: String): A

    inline def separation(inline acc: A): A

    inline def run[P <: Product](inline acc: A, inline caseClass: P)(using m: Mirror.ProductOf[P]): A =
      val tuple = Tuple.fromProductTyped(caseClass)
      end(rec[P, m.MirroredElemTypes](start(acc)(constValue[m.MirroredLabel]), 0)(tuple))


    inline def inlineAction[E](inline acc: A)(inline index: Int, inline elem: E, inline label: String, inline name: String, inline maxIndex: Int): A =
      inline if (index == maxIndex) element(acc)(elem, label) else separation(element(acc)(elem, label))

    inline def rec[P <: Product, T <: Tuple](inline acc: A, inline index: Int)(inline tuple: T)(using m: Mirror.ProductOf[P]): A =
      inline tuple match {
        case EmptyTuple => acc
        case _: NonEmptyTuple if constValue[index.type >= Tuple.Size[T]] => acc
        case nonEmptyTuple: NonEmptyTuple =>
          rec[P, T](
            inlineAction[Elem[nonEmptyTuple.type, index.type]](acc)(
              index,
              nonEmptyTuple(index),
              constValue[Elem[m.MirroredElemLabels, index.type]].asInstanceOf[String],
              constValue[m.MirroredLabel],
              constValue[Size[T] - 1]
            ),
            index + 1
          )(tuple)(using m)
      }
  }


  case object PekaPrinter extends TupleForeachBase {
    override inline def iteration[E](inline e: E): Unit = println(e.toString + "peka")
  }

  case object ToString extends TupleFoldBase[StringBuilder] {
    override inline def iteration[E](inline builder: StringBuilder, inline elem: E) =
      builder.append(elem.toString).append(", ")
  }

  case object ShowDeriver extends CaseClassDeriveBase[StringBuilder] {
    inline def start(inline acc: StringBuilder)(name: String): StringBuilder =
      acc.append(name).append("(")

    inline def end(inline acc: StringBuilder): StringBuilder =
      acc.append(")")

    inline def element[E](inline acc: StringBuilder)(inline elem: E, inline label: String): StringBuilder =
      acc.append(label).append(": ").append(elem.toString)

    inline def separation(inline acc: StringBuilder): StringBuilder =
      acc.append(", ")
  }

  sealed trait PekaTrait

  case class Entry(m:String) extends PekaTrait
  case class Entry2(m:Int) extends PekaTrait

  case class Test(a: Int, b:String, t: PekaTrait, t2: PekaTrait)

  def main(args: Array[String]): Unit = {
    //    import Pekakek._
    //    println(ToString.run(StringBuilder(), (1, 2, "pekakek")).toString)

    println(ShowDeriver.run(StringBuilder(), Test(1,"tip", Entry("hol"), Entry2(1))))
  }
}
