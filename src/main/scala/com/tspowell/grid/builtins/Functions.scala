package com.tspowell.grid.builtins

import java.lang.{Object, Integer}
import java.time.{Duration, LocalDate}

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.Table
import com.tspowell.grid.model.column.Expression

import scala.Option

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

case class Else(lhs: Expression, rhs: Expression) extends Expression(lhs, rhs) {
  override def perform(table: Table, row: Row): Object = {
    lhs.perform(table, row) match {
      case value: Object if Option(value).isEmpty =>
        rhs.perform(table, row)
      case left =>
        left
    }
  }
}