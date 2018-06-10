import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._
import org.homermultitext.edmodel._
import java.io.PrintWriter


// Create a text repository.
val catalog = "editions/catalog.cex"
val citation = "editions/citation.cex"
val editions = "editions"

val textRepo = TextRepositorySource.fromFiles(catalog, citation, editions)

val corpus = Corpus(textRepo.corpus.nodes.filterNot((_.urn.toString.contains("ref"))))
//val tokens = TeiReader.fromCorpus(textRepo.corpus)

case class StringCount(s: String, count: Int)

case class StringOccurrence(urn: CtsUrn, s: String)


def profileTokens(tokens: Vector[TokenAnalysis]) {
  val tokenTypes = tokens.map(_.analysis.lexicalCategory).distinct
  println("Token types:")
  for (ttype <- tokenTypes) {
    println("\t" + ttype + ": " + tokens.filter(_.analysis.lexicalCategory == ttype).size + " tokens.")
  }
}

def tokenHisto(tokens: Vector[TokenAnalysis]) : Vector[StringCount] = {
  val strs = tokens.map(_.readWithAlternate.text)
  val grouped = strs.groupBy(w => w).toVector
  val counted =  grouped.map{ case (k,v) => StringCount(k,v.size) }
  counted.sortBy(_.count).reverse
}

def tokenIndex(tokens: Vector[TokenAnalysis]) : Vector[String] = {
  def stringSeq = tokens.map( t => StringOccurrence(t.analysis.editionUrn, t.readWithAlternate.text))
  def grouped = stringSeq.groupBy ( occ => occ.s).toVector
  val idx = for (grp <- grouped) yield {
    val str = grp._1
    val occurrences = grp._2.map(_.urn)
    val flatList = for (occurrence <- occurrences) yield {
      str + "#" + occurrence.toString
    }
    flatList
  }
  idx.flatten
}

def wordList(tokens: Vector[TokenAnalysis]): Vector[String] = {
  tokens.map(_.analysis.readWithAlternate).distinct
}


def profileCorpus (c: Corpus) = {
  println("Citable nodes:  " + c.size)
  val tokens = TeiReader.fromCorpus(c)
  profileTokens(tokens)
  val lexTokens = tokens.filter(_.analysis.lexicalCategory == LexicalToken)
  val words = wordList(lexTokens)
  new PrintWriter("wordlist.txt"){ write(words.mkString("\n")); close;}
  val idx = tokenIndex(lexTokens)
  new PrintWriter("wordindex.txt"){ write(idx.mkString("\n")); close;}

  println("\n\nWrote index of all lexical tokens in file 'wordindex.txt'.")
  println("Wrote list of unique lexical token forms in file 'wordlist.txt'")
}
