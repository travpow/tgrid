package com.tspowell.grid.containers

import com.tspowell.grid.model.TObject
import com.tspowell.grid.model.column.TColumn

case class TRow(cellValues: TObject*) extends TObject {
  private val cells = cellValues.toArray

  def orSetDefaults(columns: Map[String, TColumn[_]], columnsByIndex: IndexedSeq[String]): TRow = {
    val cellValues = columnsByIndex.zipWithIndex.map { case (name: String, index: Int) =>
      val colDefault = columns(name).default.orNull
      val current: Option[TObject] = if (index < cells.length)
        Some(cells(index))
      else None

      current.getOrElse(colDefault)
    }

    TRow(cellValues.toIndexedSeq: _*)
  }

  def getValues: Array[Object] = cells.map { cell =>
    Option(cell).map(_.unwrap).orNull
  }
}