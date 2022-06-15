package ShowExample

import ShowExample.NewType
import ShowExample.Loggable
import ShowExample.Loggable.*
import ShowExample.Domain.*


@main
def main = {
  println(Request("/some/page", Session("13376211"), RequestData(1, 2)).log)
}

