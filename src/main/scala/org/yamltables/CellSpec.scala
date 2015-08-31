package org.yamltables

/**
 * Cell specification.
 * @param content cell contents (any YAML type, if string - to be treated as Markdown)
 * @param align cell alignment override
 */
case class CellSpec(content: Object, align: Option[String] = None)
