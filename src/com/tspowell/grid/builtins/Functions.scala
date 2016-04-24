package com.tspowell.grid.builtins

import java.time.{Period, Duration, LocalDate}

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.{TObject, Table}
import com.tspowell.grid.model.column.Expression

case class Today() extends Expression {
  override def perform(table: Table, row: Row): Object = {
    LocalDate.now()
  }
}

case class Str(strings: Expression*) extends Expression(strings: _*) {
  override def perform(table: Table, row: Row): Object = {
    val builder = new StringBuilder

    strings.foreach { expr =>
      val stringified = expr.perform(table, row) match {
        case duration: Duration =>
          s"${duration.toDays} days"
        case otherwise =>
          otherwise.toString
      }

      builder append stringified
    }

    builder.toString
  }
}

