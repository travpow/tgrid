package com.tspowell.grid.model.column

import java.util.Date

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.{TObjectValue, Table}

import scala.annotation.tailrec

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

  def perform(table: Table, row: Row): Object = {
    convertObject(expression.perform(table, row))
  }
}

case class Add(lhs: Expression, rhs: Expression) extends Expression {
  def perform(table: Table, row: Row): Object = {
    val result: java.lang.Double = ToDouble(lhs).perform(table, row).asInstanceOf[Double] +
      ToDouble(rhs).perform(table, row).asInstanceOf[Double]

    result
  }
}

case class Get(columnName: String) extends Expression {
  def perform(table: Table, row: Row): Object = {
    Expression.getValue(table, row, columnName)
  }
}
