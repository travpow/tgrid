package com.tspowell.grid.conversions

import java.time.{LocalDate, ZoneId}

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.column.Expression
import com.tspowell.grid.model.{TObjectValue, Table}

import scala.annotation.tailrec

object ToDouble extends Conversion[java.lang.Double] {
  @tailrec
  def convert(result: Object): java.lang.Double = result match {
    case int: java.lang.Integer         => int.toDouble
    case double: java.lang.Double       => double
    case string: String                 => string.toDouble
    case date: LocalDate                => date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond.toDouble
    case obj: TObjectValue              => convert(obj.unwrap)
    case _                              => throw new IllegalArgumentException(s"Could not convert value to a double [${result.getClass}]")
  }
}

case class ToDouble(expression: Expression) extends Expression(expression) {
  def perform(table: Table, row: Row): Object = {
    ToDouble.convert(expression.perform(table, row))
  }
}