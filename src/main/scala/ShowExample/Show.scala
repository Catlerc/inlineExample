package ShowExample

import scala.collection.mutable.StringBuilder
import scala.compiletime.*
import scala.deriving.Mirror

trait Show[T] {
  def show(value: T): String
}

object Show {
  extension[T] (value: T) {
    inline def show(using inline showInstance: Show[T]) = showInstance.show(value)
  }

  private inline def derivedRecursive[FieldsType <: Tuple](inline builder: StringBuilder, inline fields: FieldsType): StringBuilder =
    inline fields match {
      case s: EmptyTuple => builder
      case s: Tuple1[headType] =>
        derivedRecursive(
          builder.append(summonInline[Show[headType]].show(s.head)),
          s.tail
        )
      case s: (headType *: _) =>
        derivedRecursive(
          builder
            .append(summonInline[Show[headType]].show(s.head))
            .append(", "),
          s.tail
        )
    }

  inline def derived[T <: Product](using product: Mirror.ProductOf[T]): Show[T] =
    caseClass =>
      derivedRecursive(StringBuilder()
        .append(constValue[product.MirroredLabel] + "("), Tuple.fromProductTyped(caseClass))
        .append(")").toString()


  inline given Show[Int] = _.toString

  inline given Show[String] = str => s"\"$str\""
}
