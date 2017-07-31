package engine

trait CommandParser {

  def buildCommand(line: String): Either[Error, Command] = line match {
    case ":quit"       => Right(QuitCmd)
    case query: String => Right(SearchCmd(query))
    case unsupported   => Left(CmdError(s"Unsupported: $unsupported"))
  }

}