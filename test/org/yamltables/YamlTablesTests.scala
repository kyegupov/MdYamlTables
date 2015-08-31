package org.yamltables

import org.pegdown.PegDownProcessor
import org.scalatest._

class YamlTablesTests extends FlatSpec with Matchers {

  val MARKDOWN_PROCESSOR = new PegDownProcessor()
  val MARKDOWN_RENDERER = (o : Object) => MARKDOWN_PROCESSOR.markdownToHtml(o.toString)

  val SIMPLE_RENDERER = (o : Object) => xml.Utility.escape(o.toString)

  "Empty tablespec" should "produce empty table" in {
    val yaml = "{cols:[], data:[]}"
    assert(renderSimple(yaml) == "<table><tr></tr></table>")
  }

  "One-cell tablespec" should "produce one-cell table" in {
    val yaml = """{cols:["foo"], data:["foo": "bar"]}"""
    assert(renderSimple(yaml) == "<table><tr><th>foo</th></tr><tr><td>bar</td></tr></table>")
  }

  "Markdown markup" should "be rendered in table cells" in {
    val yaml = """{cols:["foo"], data:["foo": "**bar**"]}"""
    assert(renderMarkdown(yaml) ==
      "<table><tr><th><p>foo</p></th></tr><tr><td><p><strong>bar</strong></p></td></tr></table>")
  }

  "An example table" should "render correctly" in {
    val yaml = """
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
      Prince: *yes"""

    val expectedRendering =
      """<table>
        |<tr>
        |<th><p>Property</p></th><th><p>Trident</p></th><th><p>Gecko</p></th><th><p>WebKit</p></th>
        |<th><p>KHTML</p></th><th><p>Presto</p></th><th><p>Prince</p></th>
        |</tr>
        |<tr>
        |<td><p>@import</p></td><td align="center"><p>7.0</p></td><td align="center" rowspan=2><p>1.0</p></td>
        |<td align="right" rowspan=2><p>85</p></td>
        |<td align="center" rowspan=2><p><strong>Yes</strong></p></td><td align="center" rowspan=2><p>1.0</p></td>
        |<td align="center" rowspan=2><p><strong>Yes</strong></p></td></tr><tr><td><p>/*Comment/</p></td>
        |<td align="center"><p>3.0</p></td>
        |</tr>
        |</table>""".stripMargin.replaceAllLiterally("\n", "")
    assert(renderMarkdown(yaml) == expectedRendering)
  }

  def renderSimple(yaml: String) = YamlTables.renderTable(YamlTables.parseYamlAsMap(yaml), SIMPLE_RENDERER)

  def renderMarkdown(yaml: String) = YamlTables.renderTable(YamlTables.parseYamlAsMap(yaml), MARKDOWN_RENDERER)
}
