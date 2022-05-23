package ShowExample

import ShowExample.NewType
import ShowExample.Loggable
import ShowExample.Loggable.*
import ShowExample.Domain.*


@main
def main = {
  println(Request("/some/page", RequestData(1, 2), Session("13376211")).log)
}

