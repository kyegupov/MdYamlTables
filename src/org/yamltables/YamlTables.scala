package org.yamltables

import java.util

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object YamlTables {

  /**
   * Renders table as HTML.
   * @param tableSpec table description as YAML map
   * @param renderer cell contents renderer, should output HTML
   * @return
   */
  def renderTable(tableSpec: Map[String, Object], renderer: Object => String): String = {
    val cols = getCols(tableSpec)
    val data = getData(tableSpec)

    val outHeads = new StringBuilder()
    val outData = new StringBuilder()

    outHeads.append("<tr>")
    for (colSpec <- cols) {
      outHeads.append(s"<th>${renderer(colSpec.title)}</th>")
    }
    outHeads.append("</tr>")

    val vmergesTill = mutable.Map[ColSpec, Int]()
    for ((row, rowIndex) <- data.zipWithIndex) {
      outData.append("<tr>")
      for (colSpec <- cols) {
        val skip = vmergesTill.get(colSpec) match {
          case None => false
          case Some(till) => till > rowIndex
        }
        if (!skip) {
          var rowIndex2 = rowIndex + 1
          while (rowIndex2 < data.size && data(rowIndex2).get(colSpec.id) == row.get(colSpec.id)) {
            rowIndex2 += 1
          }
          vmergesTill.put(colSpec, rowIndex2)
          outData.append(renderCell(row, colSpec, rowIndex2 - rowIndex, renderer))
        }
      }

      outData.append("</tr>")
    }

    s"<table>${outHeads.toString()}${outData.toString()}</table>"
  }

  def getData(tableSpec: Map[String, Object]) = {
    val rows = ListBuffer[Map[String, CellSpec]]()
    for (dataRow <- tableSpec("data").asInstanceOf[util.List[util.Map[String, Object]]] ) {
      val rowCells = mutable.Map[String, CellSpec]()
      for ((key, value) <- dataRow.toMap) {
        rowCells.put(key, parseCell(value))
      }
      rows.append(rowCells.toMap)
    }
    rows.toList
  }

  def parseCell(value: Object): CellSpec = {
    if (isYamlMap(value)) {
      val cellSpecMap = asYamlMap(value)
      CellSpec(cellSpecMap.get("content").get, cellSpecMap.get("align").asInstanceOf[Option[String]])
    } else {
      CellSpec(value)
    }
  }


  def renderCell(row: Map[String, CellSpec], colSpec: ColSpec, rowSpan: Int, renderer: Object => String) = {
    if (row.containsKey(colSpec.id)) {
      val cellSpec = row(colSpec.id)

      val align = cellSpec.align.orElse(colSpec.align)
      val attributes = s" align=${align.getOrElse("")} rowspan=$rowSpan"
      s"<td$attributes>${renderer(cellSpec.content)}</td>"
    } else {
      "<td></td>"
    }
  }

  def getCols(tableSpec: Map[String, Object]): List[ColSpec] = {
    val cols = ListBuffer[ColSpec]()
    val colSpecs = tableSpec("cols").asInstanceOf[util.List[Object]]
    for (colSpecEntry <- colSpecs) {
      colSpecEntry match {
        case colId: String =>
          cols.append(ColSpec(colId, colId))
        case _ => if (isYamlMap(colSpecEntry)) {
          val colNameToSpecMap = asYamlMap(colSpecEntry)
          // Has to have single element
          assert(colNameToSpecMap.size == 1)
          val colId = colNameToSpecMap.keys.head
          val colSpecMap = asYamlMap(colNameToSpecMap.values.head)
          cols.append(ColSpec(colId,
            colSpecMap.getOrElse("title", colId).asInstanceOf[String],
            colSpecMap.get("align").asInstanceOf[Option[String]],
            colSpecMap.getOrElse("mergev", false).asInstanceOf[Boolean]))
        } else {
          sys.error("Unsupported colspec: " + colSpecEntry)
        }
      }
    }
    cols.toList
  }

  def parseYamlAsMap(yamlString: String): Map[String, Object] = {
    val yamlParser = new Yaml(new SafeConstructor())
    asYamlMap(yamlParser.load(yamlString))
  }

  def isYamlMap(o : Object) = o.isInstanceOf[java.util.Map[String @unchecked, Any @unchecked]]

  def asYamlMap(o : Object) = o.asInstanceOf[java.util.Map[String, Object]].toMap
}

