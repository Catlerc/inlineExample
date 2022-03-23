package ShowExample

object Domain {
  type Session = Session.Type

  object Session extends NewType[String] {
    inline given(using srtShow: JsonEncoder[String]): JsonEncoder[Session] = str => srtShow.encode(str.value.take(3) + "*" * (str.value.length - 3))
  }

  case class RequestData(arg1: Int, arg2: Int)
    derives JsonEncoder

  case class Request(path: String, data: RequestData, session: Session)
    derives JsonEncoder
}
