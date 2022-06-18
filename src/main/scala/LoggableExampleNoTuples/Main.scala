package LoggableExampleNoTuples

import LoggableExampleNoTuples.NewType
import LoggableExampleNoTuples.Loggable
import LoggableExampleNoTuples.Loggable.*
import LoggableExampleNoTuples.Domain.*


@main
def main = {
  println(Request("/some/page", Session("13376211"), RequestData(1, 2)).log)
}

