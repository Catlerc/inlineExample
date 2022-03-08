package ShowExample

import ShowExample.NewType
import ShowExample.Show
import ShowExample.Show.*
import ShowExample.Domain.*


@main
def main = {
  println(Request("/some/page", RequestData(1, 2), Session("13376211")).show)
}

