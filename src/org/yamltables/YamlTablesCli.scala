package org.yamltables

import org.pegdown.PegDownProcessor

import scala.reflect.io.File

object YamlTablesCli {

  val htmlHeader =
    """
    <html><head><style>td {border: 1px solid black; border-collapse: collapse}</style></head><body>"""

  val text = """
cols:
    - Property
    - Trident: &valuecol
        align: center
        mergev: true
    - Gecko: *valuecol
    - WebKit: *valuecol
    - KHTML: *valuecol
    - Presto: *valuecol
    - Prince: *valuecol
data:
    - Property: "@import"
      Trident: 7.0
      Gecko: 1.0
      WebKit: {align: right, content: 85}
      KHTML: &yes "**Yes**"
      Presto: 1.0
      Prince: *yes
    - Property: "/*Comment/"
      Trident: 3.0
      Gecko: 1.0
      WebKit: {align: right, content: 85}
      KHTML: *yes
      Presto: 1.0
      Prince: *yes
             """

  def main(args: Array[String]) {
    val processor = new PegDownProcessor()

    val table = YamlTables.renderTable(YamlTables.parseYamlAsMap(text), o => processor.markdownToHtml(o.toString))

    File("/tmp/table.html").writeAll(htmlHeader + table)
  }
}

