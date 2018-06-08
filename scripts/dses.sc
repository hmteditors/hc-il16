import edu.holycross.shot.cite._
import edu.holycross.shot.dse._
import edu.holycross.shot.scm._
import org.homermultitext.hmtcexbuilder._
import edu.holycross.shot.cex._
import java.io.PrintWriter
import edu.holycross.shot.citeobj._

val libDir = "header"
val dseDir = "dse"
val tbsDir = "codices"

val libHeader = DataCollector.compositeFiles(libDir, "cex")
val dseCex = DataCollector.compositeFiles(dseDir, "cex")
val codicesCex = DataCollector.compositeFiles(tbsDir, "cex")

val codexLibCex = libHeader + "\n" + codicesCex
//val codexLib = CiteLibrary(codexLibCex, "#", ",")

val records = dseCex.split("\n").filterNot(_.contains("passage#")).toVector

val baseUrn = "urn:cite2:hmt:tempDse.temp:"
val dseRecords = for ((record, count) <- records.zipWithIndex) yield {
  s"${baseUrn}_${count}#Temporary DSE record ${count}#${record}"
}
val srcAll = libHeader + dseRecords.drop(1).mkString("\n")
val dse = DseVector(srcAll)


val codexRepo =  CiteCollectionRepository(codicesCex)
val codexUrns = codexRepo.collections.toSeq

val ict = "http://www.homermultitext.org/ict2/"


val u = Cite2Urn("urn:cite2:hmt:msA.v1:220r")


def mdForPage(u: Cite2Urn, dse: DseVector): String = {
  val md = s"# DSE relations of page ${u}\n\nDSE relations of [${u.objectComponent}](${dse.ictForSurface(u)})"
  md
}

/** Writes a markdown file with a link to ICT2
* view of a requested page.  The output file is named
* "dse-COLLECTION-OBJEct.md".
*
* @param pageUrn URN of page
*/
def pageView(pageUrn: String) : Unit= {
  val u = Cite2Urn(pageUrn)
  val md = mdForPage(u, dse)
  new PrintWriter("validation/dse-" + u.collection + "-" + u.objectComponent + ".md"){ write (md); close}
  println("Markdown file linked to ICT2 is in validation directory: dse-" + u.collection + "-" + u.objectComponent + ".md")
}


println("\n\nCreate a view for a given page:")
println("\n\tpageView(PAGEURN)\n\n")




/*
val mdReport = for (c <- codexUrns) yield {
  val hdr = s"## DSE relations for manuscript ${c}\n\n"
  val pgSeq = codexRepo.data ~~ c

  val linkList = for (pg <- pgSeq.data) yield {
    val dseLinks = dse.ictForSurface(pg.urn, ict)
    if (dseLinks == ict) {
      "NEED IMAGE FOR " + pg.urn
    } else {
      dseLinks
    }
  }
  hdr + linkList.mkString("\n")
}
*/
