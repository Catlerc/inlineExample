package LoggableExampleNoTuples

import java.lang.StringBuilder
import scala.compiletime.*
import scala.deriving.Mirror
import scala.compiletime.ops.int.*

trait Loggable[T] {
  def log(value: T): String
}

object Loggable {
  extension[T] (value: T) {
    def log(using instance: Loggable[T]) = instance.log(value)
  }

  private inline def derivedRecursive[FieldsType <: NonEmptyTuple, N <: Int](inline builder: StringBuilder, inline fields: FieldsType): StringBuilder =
    inline erasedValue[N] match {
      case index: (Tuple.Size[FieldsType] - 1) =>
        val toAppend = summonInline[Loggable[Tuple.Elem[FieldsType, index.type ]]].log(fields(index))
        builder.append(toAppend)
      case _ =>
        inline val index = constValue[N]
        val toAppend = summonInline[Loggable[Tuple.Elem[FieldsType, index.type]]].log(fields(index))
        derivedRecursive[fields.type, S[N]](
          builder.append(toAppend).append(", "),
          fields
        )
    }

  inline def derived[T <: Product](using mirror: Mirror.ProductOf[T]): Loggable[T] =
    caseClass =>
      inline Tuple.fromProductTyped(caseClass) match {
        case EmptyTuple => constValue[mirror.MirroredLabel] + "{}"
        case fields: NonEmptyTuple =>
              derivedRecursive[fields.type, 0](
                StringBuilder().append(constValue[mirror.MirroredLabel]).append("{"),
                fields
              ).append("}").toString
      }


  given Loggable[Int] = int => s"Int{$int}"

  given Loggable[String] = str => s"String{$str}"
}
