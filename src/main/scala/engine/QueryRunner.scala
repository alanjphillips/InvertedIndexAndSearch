package engine

import scala.collection.immutable.ListMap

trait QueryRunner {

  def performSearch(query: String, index: InvertedIndex): Map[String, Int] = {
    val searchWords = splitQuery(query)

    val allMatchesSet: Set[DocumentWordMatch] = searchWords.foldLeft(Set.empty[DocumentWordMatch]) {
      (docMatchesPerWordAcc, nextSearchWord) => {                                                                       // Build a Set of Doc->SearchWord matches
        val docMatchesPerWordSet: Set[DocumentWordMatch] = index(nextSearchWord).map(fileName => DocumentWordMatch(fileName, nextSearchWord))   // look up index producing Set[DocumentWordMatch] for each SearchWord
        docMatchesPerWordAcc ++ docMatchesPerWordSet                                                                    // Add Set[DocumentWordMatch] per SearchWord to accumulated Set, since this is a fold, this will happen for all SearchWords
      }
    }

    val matchesRanking: Map[String, Int] =
      allMatchesSet.groupBy(docWord => docWord.document)                                                                // Perform a GroupBy document name on allMatchesSet: Set[DocumentWordMatch] giving Map(docName -> Set of Word+Doc matches)
        .mapValues(wordsInFile =>                                                                                       // Map over values only of Map[DocName, Set[DocumentWordMatch]] giving Map[DocName, Size of Set[DocumentWordMatch]]
          ((wordsInFile.size.toDouble / searchWords.size.toDouble) * 100).toInt                                         // Very simple Ranking method
        )

    ListMap(matchesRanking.toList.sortBy(_._2)(Ordering.Int.reverse):_*).take(10)                                       // Sort by highest ranking percentage descending, limit to max of 10 items
  }

  private def splitQuery(query: String) = query.trim.split(" ").toSet

}
