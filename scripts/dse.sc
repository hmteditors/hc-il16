import edu.holycross.shot.cite._
import edu.holycross.shot.dse._
import edu.holycross.shot.scm._
import org.homermultitext.hmtcexbuilder._
import edu.holycross.shot.cex._
import java.io.PrintWriter
import edu.holycross.shot.citeobj._


// Header file with collection info
// we need for DSE work.
val libDir = "header"
// Directory with DSE data:
val dseDir = "dse"
// URL for ICT tool:
val ict = "http://www.homermultitext.org/ict2/"



val libHeader = DataCollector.compositeFiles(libDir, "cex")
val dseCex = DataCollector.compositeFiles(dseDir, "cex")


val records = dseCex.split("\n").filterNot(_.contains("passage#")).toVector

// This value must agree with header data in header/1.dse-prolog.cex.
val baseUrn = "urn:cite2:validate:tempDse.temp:"
val dseRecords = for ((record, count) <- records.zipWithIndex) yield {
  s"${baseUrn}validate_${count}#Temporary DSE record ${count}#${record}"
}

val srcAll = libHeader + dseRecords.mkString("\n")
val dse = DseVector(srcAll)


def plural[T](v: Set[T]): String = {
  if (v.size == 1) { "s" } else {""}
}
def mdForPage(u: Cite2Urn, dse: DseVector): String = {
  val md = StringBuilder.newBuilder
  val errors = StringBuilder.newBuilder
  md.append(s"# DSE report for `${u}`\n\n")
  md.append("## Automated validation\n\n")
  md.append("Validation for **consistency** of references:\n\n")

  val  tbsTxts = dse.textsForTbs(u)


  val imgs = dse.imagesForTbs(u)
  if (imgs.size != 1) {
    errors.append(s"- Error in indexing:  ${imgs.size} image${plural(imgs)} indexed to page ${u}\n")
  } else {
    md.append(s"-  `${u.objectComponent}` is indexed to reference image `${imgs.head}`\n")
    val imgTxts = dse.textsForImage(imgs.head)
    if (imgTxts.size == tbsTxts.size) {
        md.append(s"- **${imgTxts.size}** text passages are indexed to ${imgs.head.objectComponent}\n")
        md.append(s"-  **${tbsTxts.size}** text passages are indexed to ${u.objectComponent}\n")
    } else {
      errors.append(s"- Error in indexing: ${imgTxts.size} text passages indexed top image ${imgs.head.objectComponent}, but ${tbsTxts.size} passages indexed to page ${u.objectComponent}\n")
    }


    if (errors.nonEmpty) {
      md.append("\n##Errors\n\n" + errors.toString + "\n\n")
    } else { md.append("\nResults are consistent: no errors found.\n\n")}

    md.append("## Human verification\n\n")
    md.append(s"To check for **completeness** of coverage, please review [all DSE relations of page ${u.objectComponent} in ICT2](${dse.ictForSurface(u)}.)\n\n")

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
val dseAll = {
  for (c <- codexUrns) yield {
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
}*/
