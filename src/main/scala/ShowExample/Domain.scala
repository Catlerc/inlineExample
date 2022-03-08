package ShowExample

object Domain {
  type Session = Session.Type

  object Session extends NewType[String] {
    inline given(using srtShow: Show[String]): Show[Session] = str => srtShow.show(str.value.take(3) + "*" * (str.value.length - 3))
  }

  case class RequestData(arg1: Int, arg2: Int)
    derives Show

  case class Request(path: String, data: RequestData, session: Session)
    derives Show
}
