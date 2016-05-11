package com.tspowell.grid.builtins

import java.lang.Double
import java.time.{Duration, LocalDate}

import com.tspowell.grid.containers.Row
import com.tspowell.grid.conversions.{ToDate, ToDouble}
import com.tspowell.grid.model.Table
import com.tspowell.grid.model.column.Expression

case class Add(lhs: Expression, rhs: Expression) extends Expression(lhs,rhs) {
  def perform(table: Table, row: Row): Object = {
    val left = lhs.perform(table, row)
    val right = rhs.perform(table, row)

    (left, right) match {
      case (date: LocalDate, duration: Duration) =>
        date.plusDays(duration.toDays)
      case (duration: Duration, date: LocalDate) =>
        date.plusDays(duration.toDays)
      case (_, _) =>
        ToDouble.convert(left) + ToDouble.convert(right): Double
    }
  }
}

case class Subtract(lhs: Expression, rhs: Expression) extends Expression(lhs, rhs) {
  def perform(table: Table, row: Row): Object = {
    lhs.perform(table, row) match {
      case fromDate: LocalDate =>
        val untilDate = ToDate(rhs).perform(table, row).asInstanceOf[LocalDate]
        Duration.between(untilDate.atStartOfDay(), fromDate.atStartOfDay())
      case _ =>
        val left = ToDouble(lhs).perform(table, row)
        val right = ToDouble(rhs).perform(table, row)

        left.asInstanceOf[Double] - right.asInstanceOf[Double]: Double
    }
  }
}

case class Mul(lhs: Expression, rhs: Expression) extends Expression(lhs,rhs) {
  def perform(table: Table, row: Row): Object = {
    val left = ToDouble(lhs).perform(table, row)
    val right = ToDouble(rhs).perform(table, row)

    left.asInstanceOf[Double] * right.asInstanceOf[Double]: Double
  }
}

case class Div(lhs: Expression, rhs: Expression) extends Expression(lhs, rhs) {
  def perform(table: Table, row: Row): Object = {
    val left = ToDouble(lhs).perform(table, row)
    val right = ToDouble(rhs).perform(table, row)

    if (right.asInstanceOf[Double] == 0.0) {
      Double.NaN: Double
    } else {
      left.asInstanceOf[Double] / right.asInstanceOf[Double]: Double
    }
  }
}

case class Max(lhs: Expression, rhs: Expression) extends Expression(lhs, rhs) {
  override def perform(table: Table, row: Row): Object = {
    val left = ToDouble(lhs).perform(table, row).asInstanceOf[Double]
    val right = ToDouble(rhs).perform(table, row).asInstanceOf[Double]

    Math.max(left, right): Double
  }
}
