package ShowExample

object Domain {
  type Session = Session.Type

  object Session extends NewType[String]
  {
    given Loggable[Session] = _ => "Session{MASKED}"
  }

  case class RequestData(arg1: Int, arg2: Int)
    derives Loggable

  case class Request(path: String, session: Session, data: RequestData)
    derives Loggable
}
