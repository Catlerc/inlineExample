


@main
def inlineVal(): Unit = {
  val variable = 1
  inline val inlinedVariable = 2

  println(variable)
  println(inlinedVariable)
}

@main
def inlineDef(): Unit = {
  def log(arg: String): Unit =
    println("[LOG] " + arg)

  inline def inlinedLog(arg: String): Unit =
    println("[LOG] " + arg)

  log("just log")
  inlinedLog("inlined log!")
}

@main
def inlineIf(): Unit = {
  inline val logLevel = 1

  inline def log(arg: String, level: Int): Unit =
    inline if (level >= logLevel) println("[LOG] " + arg) else ()

  log("debug log", 0)
  log("info log", 1)
}

@main
def inlineMatching(): Unit = {
  inline def triple(inline value: String | Int): Any =
    inline value match {
      case intValue: Int => intValue * 3
      case stringValue: String => stringValue * 3
    }

  println(triple("M")) // MMM
  println(triple(3)) // 9

  //println(triple("M").charAt(0)) // value charAt is not a member of Any
  //println(triple(3) + 42)        // value + is not a member of Any
}

@main
def inlineTransparent(): Unit = {
  transparent inline def triple(inline value: String | Int): Any =
    inline value match {
      case intValue: Int => intValue * 3
      case stringValue: String => stringValue * 3
    }

  println(triple("M").charAt(0)) // M
  println(triple(3) + 4) // 13
}



