package LoggableExample

import LoggableExample.NewType
import LoggableExample.Loggable
import LoggableExample.Loggable.*
import LoggableExample.Domain.*


@main
def main = {
  println(Request("/some/page", Session("13376211"), RequestData(1, 2)).log)
}

