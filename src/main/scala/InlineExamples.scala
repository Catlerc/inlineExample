@main
def inlineVal(): Unit = {
  inline val constant = 2
  println(constant)
}

@main
def inlineDef(): Unit = {
  def getString() = "test"

  inline def inlinedLog(arg: String) =
    println("[LOG] " + arg)

  inlinedLog(getString())
}

@main
def inlineArgs(): Unit = {
  def getString() = "test"

  inline def inlinedLog(inline arg: String) =
    println("[LOG] " + arg)

  inlinedLog(getString())
}

@main
def inlineIf(): Unit = {
  inline def log(arg: String, inline level: Int): Unit =
    inline if (level > 0) println("[LOG] " + arg)

  log("debug log", 0)
  log("info log", 1)
}

@main
def inlineMatching(): Unit = {
  inline def triple(inline value: String | Int): Any =
    inline value match {
      case intValue: Int => intValue * 3
      case stringValue: String => stringValue.repeat(3)
    }

  println(triple("M")) //prints: MMM
  println(triple(3))   //prints: 9

  //println(triple("M").charAt(0)) // value charAt is not a member of Any
  //println(triple(3) + 42)        // value + is not a member of Any
}

@main
def inlineTransparent(): Unit = {
  transparent inline def foo(inline value: Int): Any =
    if (value == 42) "Forty two" else value

  println(foo(42)(0))   //prints: F
  println(foo(112) * 3) //prints: 336
}