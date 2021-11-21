package utils

object DataTransformationUtils {

  def parse(linkHeader: IndexedSeq[String]): Map[String, String] = {
    assert(linkHeader.size == 1)
    linkHeader.head.split(',').map { linkEntry =>
      val entryPart = linkEntry.split(';')
      assert(entryPart.size == 2)
      val rel = entryPart(1).replace(" rel=\"", "").replace("\"", "")
      val url = entryPart(0).replace("<", "").replace(">", "")
      (rel, url)
    }.toMap
  }

}
