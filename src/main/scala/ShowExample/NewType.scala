package ShowExample

trait NewType[RealType] {
  opaque type Type = RealType

  inline def apply(value: RealType): Type = value

  extension (value: Type) inline def value: RealType = value
}
