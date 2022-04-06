package ShowExample

//import ShowExample.NewType
//import ShowExample.Show
//import ShowExample.Show.*
//import ShowExample.Domain.*

import scala.compiletime.*
import scala.compiletime.ops.int._
import scala.compiletime.ops.any._
import scala.deriving.Mirror

trait FakeShow[T] {
  def show(t: T): String
}

trait ManualShow[T] {
  def show(t: T): String
}

object FakeShow {
  inline def derived[T <: Product](using product: Mirror.ProductOf[T]): FakeShow[T] =
    caseClass =>
      anyForeach(Tuple.fromProductTyped(caseClass), 0, constValue[product.MirroredLabel])(new StringBuilder).toString()

  extension[T] (value: T) {
    inline def fakeShow(using inline showInstance: FakeShow[T]) = showInstance.show(value)
  }
}

import FakeShow._


//trait FakeShowAppended[T] {
//  def showAppended(builder: StringBuilder, t: T): StringBuilder
//}

//transparent inline def foreach[T <: NonEmptyTuple](inline t: T, inline func: [t] => t => Unit, inline n: Int): Unit = {
//  inline erasedValue[n.type] match {
//    case _ if constValue[n.type < Tuple.Size[t.type]] =>
//      func[Tuple.Elem[t.type, n.type]](t(n))
//      foreach[T](t, func, n + 1)
//    case _ => ()
//  }
//}

//transparent inline def makeShow[Elem <: Product : Mirror.ProductOf](inline e: Elem, inline builder: StringBuilder): StringBuilder = {
//  foreach(Tuple.fromProductTyped(e), 0)(builder)
//}

transparent inline def getNameOfType[T]: String = {
  inline val asg = summonInline[Mirror.Of[T]]
  constValue[asg.MirroredLabel]
}



transparent inline def inlineShow[Elem](e: Elem, inline builder: StringBuilder, inline name: String): StringBuilder = summonFrom {

  case f: ManualShow[Elem] =>
    val str = f.show(e)
    builder.append(str)
  case m: Mirror.ProductOf[Elem with Product] =>
    inline Tuple.fromProductTyped[Elem with Product](e.asInstanceOf[Elem with Product])(using m) match {
      case rr: NonEmptyTuple => foreach(rr, 0, constValue[m.MirroredLabel])(builder)
      //      case _ =>
    }
  case _ =>
    summonInline[ManualShow[Elem]]
    ??? // make error
}

transparent inline def anyForeach[T <: Tuple](inline t: T, inline n: Int, inline name: String)(inline builder: StringBuilder): StringBuilder = {
  inline t match {
    case nt: NonEmptyTuple => foreach(nt, n, name)(builder)
    case et: EmptyTuple => builder.append(name).append("()")
  }
}

transparent inline def foreach[T <: NonEmptyTuple](t: T, n: Int, inline name: String)(inline builder: StringBuilder): StringBuilder = {
  inline erasedValue[n.type] match {
    case _ if constValue[n.type == 0] =>
      foreach[T](t, n + 1, name)(inlineShow[Tuple.Elem[t.type, n.type]](t(n), builder.append(name + "("), name)).append(")")
    case _ if constValue[n.type == Tuple.Size[t.type] - 1] =>
      foreach[T](t, n + 1, name)(inlineShow[Tuple.Elem[t.type, n.type]](t(n), builder.append(", "), name))
    case _ if constValue[n.type < Tuple.Size[t.type]] =>
      foreach[T](t, n + 1, name)(inlineShow[Tuple.Elem[t.type, n.type]](t(n), builder.append(", "), name))
    case _ => builder
  }
}


object PekaMAin {
  given ManualShow[Int] = _.toString

  given ManualShow[String] = a => s"\"$a\""

  case class Jeka(a: String) derives FakeShow

  case class Jeka2(j: Jeka) derives FakeShow

  case class MekaCaseClass(j: Jeka2, s: String, s2: String, d: Int, g: Int) derives FakeShow

  @main
  def main = {


    //  println(foreach(a, 0, "poh")(builder).toString())
    val res = MekaCaseClass(Jeka2(Jeka("log")), "a", "b", 1, 42).fakeShow
    println(res)
  }


}
