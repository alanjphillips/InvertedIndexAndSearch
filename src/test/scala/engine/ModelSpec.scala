package engine

import org.scalatest.{Matchers, WordSpec}

class ModelSpec extends WordSpec with Matchers {

  "SearchCmd" should {
    "create a SearchCmd" in {
      val queryTerms = "term1 term2 term3"
      SearchCmd("term1 term2 term3").query shouldBe queryTerms
    }
  }

  "CmdError" should {
    "create a CmdError" in {
      val errorMessage = "error"
      CmdError(errorMessage).reason shouldBe errorMessage
    }
  }

  "DocumentWordMatch" should {
    "create a DocumentWordMatch" in {
      val doc = "river.txt"
      val word = "water"
      val docWordMatch = DocumentWordMatch(doc, word)
      docWordMatch.document shouldBe doc
      docWordMatch.word shouldBe word
    }
  }

}
