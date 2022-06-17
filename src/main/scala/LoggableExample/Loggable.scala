package LoggableExample

import java.lang.StringBuilder
import scala.compiletime.*
import scala.deriving.Mirror

trait Loggable[T] {
  def log(value: T): String
}

object Loggable {
  extension[T] (value: T) {
    def log(using instance: Loggable[T]) = instance.log(value)
  }

  private inline def derivedRecursive[FieldsType <: Tuple](inline builder: StringBuilder, inline fields: FieldsType): StringBuilder =
    inline fields match {
      case _: EmptyTuple => builder
      case tuple: Tuple1[headType] =>
        val toAppend = summonInline[Loggable[headType]].log(tuple.head)
        derivedRecursive(
          builder.append(toAppend),
          tuple.tail
        )
      case tuple: (headType *: _) =>
        val toAppend = summonInline[Loggable[headType]].log(tuple.head)
        derivedRecursive(
          builder
            .append(toAppend)
            .append(", "),
          tuple.tail
        )
    }

  inline def derived[T <: Product](using mirror: Mirror.ProductOf[T]): Loggable[T] =
    caseClass =>
      derivedRecursive(StringBuilder()
        .append(constValue[mirror.MirroredLabel] + "{"), Tuple.fromProductTyped(caseClass))
        .append("}").toString


  given Loggable[Int] = int => s"Int{$int}"

  given Loggable[String] = str => s"String{$str}"
}
