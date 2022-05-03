package ShowExample

import java.lang.StringBuilder
import scala.compiletime.*
import scala.deriving.Mirror

trait Show[T] {
  def show(value: T): String
}

object Show {
  extension[T] (value: T) {
    def show(using showInstance: Show[T]) = showInstance.show(value)
  }

  private inline def derivedRecursive[FieldsType <: Tuple](inline builder: StringBuilder, inline fields: FieldsType): StringBuilder =
    inline fields match {
      case _: EmptyTuple => builder
      case tuple: Tuple1[headType] =>
        val toAppend = summonInline[Show[headType]].show(tuple.head)
        derivedRecursive(
          builder.append(toAppend),
          tuple.tail
        )
      case tuple: (headType *: _) =>
        val toAppend = summonInline[Show[headType]].show(tuple.head)
        derivedRecursive(
          builder
            .append(toAppend)
            .append(", "),
          tuple.tail
        )
    }

  inline def derived[T <: Product](using product: Mirror.ProductOf[T]): Show[T] =
    caseClass =>
      derivedRecursive(StringBuilder()
        .append(constValue[product.MirroredLabel] + "("), Tuple.fromProductTyped(caseClass))
        .append(")").toString()


  given Show[Int] = _.toString

  given Show[String] = str => s"\"$str\""
}
