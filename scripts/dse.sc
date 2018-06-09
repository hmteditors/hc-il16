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

//val codexLibCex = libHeader + "\n" + codicesCex
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


//val u = Cite2Urn("urn:cite2:hmt:msA.v1:220r")

def plural[T](v: Set[T]): String = {
  if (v.size == 1) { "s" } else {""}
}
def mdForPage(u: Cite2Urn, dse: DseVector): String = {
  val md = StringBuilder.newBuilder
  val errors = StringBuilder.newBuilder

  md.append(s"# DSE relations of page ${u}\n\nDSE relations of [${u.objectComponent}](${dse.ictForSurface(u)})\n\n")
  val  tbsTxts = dse.textsForTbs(u)
  md.append(s"${tbsTxts.size} text passages indexed to ${u.objectComponent}\n\n")

  val imgs = dse.imagesForTbs(u)
  if (imgs.size != 1) {
    errors.append(s"- Error in indexing:  ${imgs.size} image${plural(imgs)} indexed to page ${u}\n\n")
  } else {
    md.append(s"${u.objectComponent} indexed to reference image ${imgs.head}\n\n")
    val imgTxts = dse.textsForImage(imgs.head)
    if (imgTxts.size == tbsTxts.size) {
        md.append("DSE relations are consistent: ")
        md.append(s"${imgTxts.size} text passages indexed to ${imgs.head.objectComponent}")
    } else {
      errors.append(s"- Error in indexing: ${imgTxts.size} text passages indexed top image ${imgs.head.objectComponent}, but ${tbsTxts.size} passages indexed to page ${u.objectComponent}\n\n")
    }

    if (errors.nonEmpty) {
      md.append("\n##Errors\n\n" + errors.toString + "\n\n")
    } else { md.append("No errors found.\n\n")}
    md
  }




  md.toString
}

/** Writes a markdown file with a link to ICT2
* view of a requested page.  The output file is named
* "dse-COLLECTION-OBJEct.md".
*
* @param pageUrn URN of page
*/
def pageView(pg: Cite2Urn, dse: DseVector) : Unit= {

  val md = mdForPage(pg, dse)
  new PrintWriter("validation/dse-" + pg.collection + "-" + pg.objectComponent + ".md"){ write (md); close}
  println("Markdown report is in validation directory: dse-" + pg.collection + "-" + pg.objectComponent + ".md")
}

def validate(pageUrn: String) : Unit = {
  val u = Cite2Urn(pageUrn)
  pageView(u, dse)
}

println("\n\nValidate DSE relations for a given page:")
println("\n\tvalidate(PAGEURN)\n\n")




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
