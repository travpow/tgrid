package com.tspowell.grid.containers

import com.tspowell.grid.model.{Table, TObject}
import com.tspowell.grid.model.column.Column

case class Row(cellValues: TObject*) extends TObject {
  private val cells = cellValues.toArray

  def orSetValue(table: Table, columns: IndexedSeq[(String,Column[_])]): Row = {
    val cellValues = columns.zipWithIndex.map { case ((_: String, column: Column[_]), index: Int) =>
      val colDefault = column.default.orNull

      val current: Option[TObject] = if (column.expression.isDefined) {
        Some(column.expression.get.perform(table, this))
      } else if (index < cells.length) {
        Some(cells(index))
      } else None

      current.getOrElse(colDefault)
    }

    Row(cellValues.toIndexedSeq: _*)
  }

  def getValues: Array[Object] = cells.map { cell =>
    Option(cell).map(_.unwrap).orNull
  }
}