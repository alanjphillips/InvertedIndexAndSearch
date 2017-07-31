package engine

import java.io._

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import engine.SearchEngineSpec._

import scala.io.Source

class SearchEngineSpec
  extends WordSpec
    with ScalaFutures
    with Matchers
    with MockitoSugar {

  "SearchEngine" should {

    "start read loop and then quit" in {
      val quit = ":quit\r\n"
      val in = new ByteArrayInputStream(quit.getBytes)
      val out = new PrintStream(new ByteArrayOutputStream)

      val searchEngine = new SearchEngine(index)(in, out) with CommandParserMock with QueryRunnerMock
      val done = searchEngine.readLoop()
      done shouldBe Done()
    }

    "prompt and read" in {
      val query = "sand\r\n"
      val in = new ByteArrayInputStream(query.getBytes)
      val out = new PrintStream(new ByteArrayOutputStream)

      val searchEngine = new SearchEngine(index)(in, out) with CommandParserMock with QueryRunnerMock
      val userInput = searchEngine.promptAndRead

      userInput shouldBe "sand"
    }

    "print result to console" in {
      val query = "sand\r\n"
      val in = new ByteArrayInputStream(query.getBytes)
      val bout = new ByteArrayOutputStream()
      val out = new PrintStream(bout)

      val searchEngine = new SearchEngine(index)(in, out) with CommandParserMock with QueryRunnerMock
      searchEngine.printToConsole(searchResult)

      val resultIn = new ByteArrayInputStream(bout.toByteArray)
      val printedOutput = Source.fromInputStream(resultIn).bufferedReader.readLine()

      printedOutput shouldBe "desert.txt : 100%"
    }

  }

}

object SearchEngineSpec {
  val index =
    InvertedIndex(
      Map(
        "sand" -> Set("desert.txt"),
        "desert" -> Set("desert.txt"),
        "river" -> Set("rivers.txt"),
        "water" -> Set("mountains.txt", "rivers.txt"),
        "mountain" -> Set("mountains.txt"),
        "high" -> Set("mountains.txt")
      )
    )

  val searchResult = Map(
    "desert.txt" -> 100
  )
}

trait CommandParserMock extends CommandParser {
  override def buildCommand(line: String): Either[Error, Command] = line match {
    case ":quit" => Right(QuitCmd)
    case query: String => Right(SearchCmd(query))
  }
}

trait QueryRunnerMock extends QueryRunner {
  override def performSearch(query: String, index: InvertedIndex): Map[String, Int] = searchResult
}
