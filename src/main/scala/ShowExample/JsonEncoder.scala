package ShowExample

import scala.collection.mutable.StringBuilder
import scala.compiletime.*
import scala.deriving.Mirror

trait JsonEncoder[T] {
  def encode(value: T): String
}

object JsonEncoder {
  extension[T] (value: T) {
    inline def encode(using inline encoder: JsonEncoder[T]) = encoder.encode(value)
  }

  private inline def derivedRecursive[FieldTypes <: Tuple, FieldLabels <: Tuple](inline builder: StringBuilder, inline fields: FieldTypes, inline labels: FieldLabels): StringBuilder =
    inline (fields, labels) match {
      case s: (EmptyTuple, EmptyTuple) => builder
      case s: (Tuple1[elemType], Tuple1[label]) =>
        derivedRecursive(
          builder
            .append('"')
            .append(s._2.head)
            .append("\": ")
            .append(summonInline[JsonEncoder[elemType]].encode(s._1.head)),
          EmptyTuple,
          EmptyTuple
        )
      case s: (elemType *: tail, elemLabel *: elemTail) =>
        derivedRecursive(
          builder
            .append('"')
            .append(s._2.head)
            .append("\": ")
            .append(summonInline[JsonEncoder[elemType]].encode(s._1.head))
            .append(", "),
          s._1.tail,
          s._2.tail
        )
    }


  inline def constValueAll[T <: Tuple]: T = {
    val res =
      inline erasedValue[T] match
        case _: EmptyTuple => EmptyTuple
        case _: (t *: ts) => constValue[t] *: constValueAll[ts]
      end match
    res.asInstanceOf[T]
  }

  inline def derived[T <: Product](using product: Mirror.ProductOf[T]): JsonEncoder[T] =
    caseClass =>
      constValueAll[product.MirroredElemLabels].toString
      derivedRecursive(StringBuilder()
        .append("{"), Tuple.fromProductTyped(caseClass), constValueAll[product.MirroredElemLabels])
        .append("}").toString()


  inline given JsonEncoder[Int] = _.toString

  inline given JsonEncoder[String] = str => s"\"$str\""
}
