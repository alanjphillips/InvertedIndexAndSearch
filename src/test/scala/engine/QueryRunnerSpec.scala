package engine

import org.scalatest.{Matchers, WordSpec}
import engine.InvertedIndex.{DocumentNameSet, Word}
import engine.QueryRunnerSpec._

class QueryRunnerSpec extends WordSpec with Matchers {

    "QueryRunner" should {

      "return results for a Search query" in {
        val query = "monkeys cats and dogs"
        val expectedResult = Map("Zoo.txt" -> 50, "BookOfMonkeys.txt" -> 25, "BookOfCats.txt" -> 25, "BookOfDogs.txt" -> 25)
        val queryRunner = new QueryRunner {}

        queryRunner.performSearch(query, invertedIndex) shouldBe expectedResult
      }

      "return empty results for a Search query" in {
        val query = "snakes"
        val expectedResult = Map.empty[Word, DocumentNameSet]
        val queryRunner = new QueryRunner {}

        queryRunner.performSearch(query, invertedIndex) shouldBe expectedResult
      }

    }

}

object QueryRunnerSpec {

  def invertedIndex =
    InvertedIndex(
      table = Map(
        "cats"    -> Set("BookOfCats.txt", "Zoo.txt"),
        "dogs"    -> Set("BookOfDogs.txt"),
        "monkeys" -> Set("BookOfMonkeys.txt", "Zoo.txt")
      )
    )

}
