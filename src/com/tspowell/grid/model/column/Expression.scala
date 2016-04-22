package com.tspowell.grid.model.column

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.Table

object Expression {
  implicit def string2Expression(name: String): Expression = {
    Get(name)
  }

  def getValue(table: Table, row: Row, name: String): Object = {
    row.cellValues(table.getColumnIndex(name))
  }
}

trait Expression extends ColumnOp {
  def perform(table: Table, row: Row): Object
}

case class L(value: Object) extends Expression {
  def perform(table: Table, row: Row): Object = {
    value
  }
}

case class Get(columnName: String) extends Expression {
  def perform(table: Table, row: Row): Object = {
    Expression.getValue(table, row, columnName)
  }
}
