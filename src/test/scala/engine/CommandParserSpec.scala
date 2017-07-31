package engine

import org.scalatest.{Matchers, WordSpec}

class CommandParserSpec extends WordSpec with Matchers {

  "CommandParser" should {

    "build a QuitCommand given a Quit formatted String" in {
      val input = ":quit"
      val parser = new CommandParser {}
      parser.buildCommand(input) shouldBe Right(QuitCmd)
    }

    "build a SearchCommand given a search String" in {
      val input = "something to find"
      val parser = new CommandParser {}
      parser.buildCommand(input) shouldBe Right(SearchCmd(input))
    }

    "return CmdError for unsupported input" in {
      val input = null
      val parser = new CommandParser{}
      parser.buildCommand(input) shouldBe Left(CmdError(s"Unsupported: $input"))
    }

  }

}
