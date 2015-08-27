package org.yamltables

/**
 * Column specification.
 * @param id column id in YAML data
 * @param title human-readable column title
 * @param align column default alignment
 * @param mergeVertical whether to merge cells in this column with the same value
 */
case class ColSpec(id: String, title: String, align: Option[String] = None, mergeVertical: Boolean = false)
