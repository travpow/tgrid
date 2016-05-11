package com.tspowell.grid.containers

import com.tspowell.grid.model.{TObjectValue, Table, TObject}
import com.tspowell.grid.model.column.Column

import scala.collection.mutable

object Row {
  // TODO: Choose function or procedural implementation

  def orSetValue(table: Table, row: Row, columns: IndexedSeq[(String,Column[_])]): Row = {
    columns.zipWithIndex.foldLeft(row) { case (accum, ((name, column), index)) =>
      val colDefault: TObject = column.default.orNull

      if (column.expression.isDefined) {
        val calculated: TObject = TObjectValue(column.expression.get.perform(table, accum))
        Row(accum.cells :+ calculated)
      } else if (index >= accum.cells.length) {
        Row(accum.cells :+ colDefault)
      } else {
        accum
      }
    }
  }

  def apply(cellValues: TObject*): Row = Row(cellValues.toArray)
}

case class Row(cells: Array[TObject]) extends TObject {
  def getValues: Array[Object] = cells.map { cell =>
    Option(cell).map(_.unwrap).orNull
  }
}