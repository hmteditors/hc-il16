import edu.holycross.shot.ohco2._
import edu.holycross.shot.cite._

// Create CEX output for a page.
//
// 1. CEX for texts.

val catalog = "editions/catalog.cex"
val citation = "editions/citation.cex"
val editions = "editions"


import scala.io.Source


val textRepo = TextRepositorySource.fromFiles(catalog, citation, editions)

// get DSE source:
val dseDir = "dse"
val dseCex = DataCollector.compositeFiles(dseDir, "cex")
