package com.tspowell.grid.model.column

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.Table

object Expression {
  implicit def string2Expression(name: String): Expression = {
    Get(name)
  }

  def getValue(table: Table, row: Row, name: String): Object = {
    val index = table.getColumnIndex(name)
    row.getValues(index)
  }
}

abstract class Expression(dependencies: Expression*) extends ColumnOp {
  def perform(table: Table, row: Row): Object
  def validate(columns: Set[String]): Unit = {
    dependencies.foreach {
      _.validate(columns)
    }
  }
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

  override def validate(columns: Set[String]): Unit = {
    if (!columns.contains(columnName)) {
      throw new IllegalArgumentException(s"Invalid column name to Get() expression: [$columnName], column must have a concrete definition in this table or a dependency table.")
    }
  }
}
