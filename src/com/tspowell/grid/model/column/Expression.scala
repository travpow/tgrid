package com.tspowell.grid.model.column

import java.util.Date

import com.tspowell.grid.containers.TRow
import com.tspowell.grid.model.{TObjectValue, TTable}

import scala.annotation.tailrec

object Expression {
  implicit def string2Expression(name: String): Expression = {
    Get(name)
  }

  def getValue(table: TTable, row: TRow, name: String): Object = {
    row.cellValues(table.getColumnIndex(name))
  }
}

trait Expression extends ColumnOp {
  def perform(table: TTable, row: TRow): Object
}

case class L(value: Object) extends Expression {
  def perform(table: TTable, row: TRow): Object = {
    value
  }
}

case class ToDouble(expression: Expression) extends Expression {

  @tailrec
  private def convertObject(result: Object): java.lang.Double = result match {
    case int: java.lang.Integer => int.toDouble
    case double: java.lang.Double => double
    case string: String => string.toDouble
    case date: Date => date.toInstant.getEpochSecond.toDouble
    case obj: TObjectValue => convertObject(obj.unwrap)
    case _ => throw new IllegalArgumentException(s"Could not convert value to a double [${result.getClass}]")

  }

  def perform(table: TTable, row: TRow): Object = {
    convertObject(expression.perform(table, row))
  }
}

case class Add(lhs: Expression, rhs: Expression) extends Expression {
  def perform(table: TTable, row: TRow): Object = {
    val result: java.lang.Double = ToDouble(lhs).perform(table, row).asInstanceOf[Double] +
      ToDouble(rhs).perform(table, row).asInstanceOf[Double]

    result
  }
}

case class Get(columnName: String) extends Expression {
  def perform(table: TTable, row: TRow): Object = {
    Expression.getValue(table, row, columnName)
  }
}
