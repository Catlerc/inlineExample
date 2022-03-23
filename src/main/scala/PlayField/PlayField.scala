//package PlayField
//
//import ShowExample.NewType
//
//import scala.compiletime.summonInline
//import scala.runtime.Nothing$
//
//trait Show[T]:
//  inline def apply(x: T): Unit
//inline def findAndRunShow[T](x: T)(using show: Show[T]): Unit =
//  show(x)
//object PlayField {
//
//
//
//  inline given Show[String] with
//    inline def apply(x: String): Unit = println(x)
//
//  inline given Show[Int] with
//    inline def apply(x: Int): Unit =
//      inline Tuple1("") match {
//        case _: Tuple1[t] =>
////          given Show[t]=summonInline[Any with Show[t]]
//          findAndRunShow[Any with t](???)
//      }
////
//
//
//  def test()(using s:Show[Int]) = findAndRunShow(2)
//
//  def main(args: Array[String]): Unit = {
//
//    test()
//  }
//}
