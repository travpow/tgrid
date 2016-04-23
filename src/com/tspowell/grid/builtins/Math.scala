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
      case (leftDate: LocalDate, rightDuration: Duration) =>
        leftDate.plusDays(rightDuration.toDays)
      case (_, _) =>
        val sum: Double = ToDouble.convert(left) + ToDouble.convert(right)
        sum
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

        val difference: Double = left.asInstanceOf[Double] - right.asInstanceOf[Double]
        difference
    }
  }
}
