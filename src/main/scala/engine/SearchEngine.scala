package engine

import java.io.{InputStream, PrintStream}

import scala.annotation.tailrec
import scala.io.Source

class SearchEngine(index: InvertedIndex)(in: InputStream, out: PrintStream)
  extends CommandParser
  with QueryRunner {

  @tailrec
  final def readLoop(): Done = {
    buildCommand(promptAndRead) match {
      case Left(error: Error) => {
        println(s"Error: ${error.reason}")
        readLoop()
      }

      case Right(QuitCmd) => Done()

      case Right(SearchCmd(query))  => {
        val results = performSearch(query, index)
        printToConsole(results)
        readLoop()
      }

      case _ => readLoop()
    }
  }

  def promptAndRead: String = {
    print("search> ")
    Source.fromInputStream(in).bufferedReader.readLine()
  }

  def printToConsole(resultRows: Map[String, Int]): Unit =
    if (resultRows.size > 0) {
      resultRows.foreach(resultEntry =>
        out.println(s"${resultEntry._1} : ${resultEntry._2}%")
      )
    } else
      out.println(s"no matches found")

}

object SearchEngine {
  def apply(index: InvertedIndex)(
            in: InputStream = System.in,
            out: PrintStream = System.out
           ): SearchEngine =
    new SearchEngine(index)(in, out)
}