package engine

import java.io.File

import engine.InvertedIndex.{DocumentNameSet, Word, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

case class InvertedIndex(table: Map[Word, DocumentNameSet] = Map.empty[Word, DocumentNameSet]) {

  def apply(searchWord: String): DocumentNameSet = table.getOrElse(searchWord, Set.empty[String])

  def hasIndexedData: Boolean = table.size > 0

}

object InvertedIndex {
  type Word =  String
  type DocumentNameSet = Set[String]

  val stopWords = Set("and", "a", "if")   // Words that are not indexed, expand this Set if necessary
}

object IndexBuilder {

  /**
    * Index file contents in serial (Not used currently)
    *
    * @param textFilePath
    * @return
    */
  def build(textFilePath: String): InvertedIndex = {
    val textFiles = filesToIndex(textFilePath)
    println(s"${textFiles.size} files read in directory $textFilePath")

    val table = textFiles.foldLeft(Map.empty[Word, DocumentNameSet]) {
      (accIndexLevel, nextFile) => {
        val indexPerFile = buildIndexForFile(nextFile)
        accIndexLevel ++ mergeDocumentSets(accIndexLevel, indexPerFile)
      }
    }
    InvertedIndex(table)
  }

  /**
    * Indexes file contents in parallel (In use)
    *
    * @param textFilePath
    * @return
    */
  def buildParallel(textFilePath: String): Future[InvertedIndex] = {
    val textFiles = filesToIndex(textFilePath)
    println(s"${textFiles.size} files read in directory $textFilePath")

    val table: Future[Map[Word, DocumentNameSet]] = textFiles.foldLeft(Future.successful(Map.empty[Word, DocumentNameSet])) {
      (accIndexLevelFut, nextFile) => {
        for {
          accIndexLevel <- accIndexLevelFut                                                       // Fold's accumulator of Map[Word, DocumentNameSet]
          indexPerFile <- buildIndexForFileFut(nextFile)
          accMergedToIndexPerFile = mergeDocumentSets(accIndexLevel, indexPerFile)
        } yield accIndexLevel ++ accMergedToIndexPerFile
      }
    }
    table map {
      InvertedIndex(_)
    }
  }

  private def buildIndexForFileFut(file: File): Future[Map[Word, DocumentNameSet]] =
    Future {
      buildIndexForFile(file)
    }

  private def buildIndexForFile(file: File): Map[Word, DocumentNameSet] = {
    val source = Source.fromFile(file)
    val wordList = source.getLines.mkString.split("[\\p{Punct}\\s]+").toList.filter(!stopWords.contains(_)) // read file, convert to String, split according to regex, filter out StopWords
    source.close
    wordList.foldLeft(Map.empty[Word, DocumentNameSet]) {                                                   // Build Map of [word -> documents that contain it]
      (accFileLevel, nextWord) =>
        accFileLevel + (nextWord -> addToDocumentSet(accFileLevel, nextWord, file.getName))                 // if word has document Set already then add it to document Set, or create a new document Set containing word
    }
  }

  private def mergeDocumentSets(accIndexLevel: Map[Word, DocumentNameSet], indexPerFile: Map[Word, DocumentNameSet]) =
    indexPerFile map {
      case (word, docSet) =>                                                                                // indexPerFile.map: in case of overall index already having this word, add file level doc set to existing doc set
        word -> (docSet ++ accIndexLevel.getOrElse(word, Set.empty[String]))
    }

  private def addToDocumentSet(table: Map[Word, DocumentNameSet], word: Word, fileName: String): DocumentNameSet =
    if (table contains word)
      table(word) + fileName
    else
      Set(fileName)

  private def filesToIndex(textFilePath: String): List[File] = {
    val dir = new File(textFilePath)
    if (dir.exists && dir.isDirectory)
      dir.listFiles.filter(_.isFile).toList
    else
      Nil
  }

}
