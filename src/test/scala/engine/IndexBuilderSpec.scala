package engine

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import engine.IndexBuilderSpec._
import engine.InvertedIndex.{DocumentNameSet, Word}

class IndexBuilderSpec
  extends WordSpec
    with ScalaFutures
    with Matchers {

  "IndexBuilder" should {

    "serially build InvertedIndex using files in given folder location" in {
      val path = getClass.getResource("/searchfiles").getPath
      IndexBuilder.build(path) shouldBe expectedIndex
    }

    "serially build empty InvertedIndex and report that zero files were found" in {
      val path = getClass.getResource("/emptysearchfiles").getPath
      IndexBuilder.build(path) shouldBe InvertedIndex(Map.empty[Word, DocumentNameSet])
    }

    "parallel build InvertedIndex using files in given folder location" in {
      val path = getClass.getResource("/searchfiles").getPath
      val indexFut = IndexBuilder.buildParallel(path)

      whenReady(indexFut)(
        result => result shouldBe expectedIndex
      )
    }

    "parallel build empty InvertedIndex and report that zero files were found" in {
      val path = getClass.getResource("/emptysearchfiles").getPath
      val indexFut = IndexBuilder.buildParallel(path)

      whenReady(indexFut)(
        result => result shouldBe InvertedIndex(Map.empty[Word, DocumentNameSet])
      )
    }

  }

}

object IndexBuilderSpec {

  val expectedIndex =
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


}