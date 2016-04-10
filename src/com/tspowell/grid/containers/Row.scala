package com.tspowell.grid.containers

import com.tspowell.grid.model.TObject
import com.tspowell.grid.model.column.Column

case class Row(cellValues: TObject*) extends TObject {
  private val cells = cellValues.toArray

  def orSetDefaults(columns: Map[String, Column[_]], columnsByIndex: IndexedSeq[String]): Row = {
    val cellValues = columnsByIndex.zipWithIndex.map { case (name: String, index: Int) =>
      val colDefault = columns(name).default.orNull
      val current: Option[TObject] = if (index < cells.length)
        Some(cells(index))
      else None

      current.getOrElse(colDefault)
    }

    Row(cellValues.toIndexedSeq: _*)
  }

  def getValues: Array[Object] = cells.map { cell =>
    Option(cell).map(_.unwrap).orNull
  }
}