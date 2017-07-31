package engine

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object Boot extends App {

  if (args.size > 0) {
    val indexFut = IndexBuilder.buildParallel(args.head)

    val bootFut = indexFut.map { index =>
      if (index.hasIndexedData)
        SearchEngine(index)().readLoop
      else
        println(s"Exiting because there is no indexed data to search")
    } recover {
      case ex => println(s"Failure occurred during indexing: ${ex.getMessage}")
    }

    Await.ready(bootFut, Duration.Inf)
  } else
    println(s"A folder location must be given as command-line argument")

}
