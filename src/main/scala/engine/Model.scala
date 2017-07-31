package engine

trait Command

trait Error {
  def reason: String
}

case class Done()

case object QuitCmd extends Command

case class SearchCmd(query: String) extends Command

case class CmdError(reason: String) extends Error

case class DocumentWordMatch(document: String, word: String)