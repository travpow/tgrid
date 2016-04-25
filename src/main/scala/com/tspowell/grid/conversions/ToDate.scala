package com.tspowell.grid.conversions

import java.time.LocalDate

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.column.Expression
import com.tspowell.grid.model.{TObjectValue, Table}

import scala.annotation.tailrec

object ToDate extends Conversion[LocalDate] {
  @tailrec
  def convert(result: Object): LocalDate = result match {
    case int: java.lang.Integer     => convert(new java.lang.Long(int.toLong))
    case long: java.lang.Long       => LocalDate.ofEpochDay(long)
    case string: String             => LocalDate.parse(string)
    case obj: TObjectValue          => convert(obj.unwrap)
    case date: LocalDate            => date
    case _                          => throw new IllegalArgumentException(s"Could not convert value to a date [${result.getClass}]")
  }
}

case class ToDate(expression: Expression) extends Expression(expression) {
  def perform(table: Table, row: Row): Object = {
    ToDate.convert(expression.perform(table, row))
  }
}