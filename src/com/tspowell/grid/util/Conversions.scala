package com.tspowell.grid.util

import java.time.{LocalDate, ZoneId}

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.column.Expression
import com.tspowell.grid.model.{TObjectValue, Table}

/**
  * Created by travis on 4/19/16.
  */
object Conversions {
  case class ToDouble(expression: Expression) extends Expression {
    def perform(table: Table, row: Row): Object = {
      convertObject(expression.perform(table, row))
    }

    protected def convertObject(result: Object): java.lang.Double = result match {
      case int: java.lang.Integer => int.toDouble
      case double: java.lang.Double => double
      case string: String => string.toDouble
      case date: LocalDate => date.atStartOfDay(ZoneId.systemDefault()).toEpochSecond.toDouble
      case obj: TObjectValue => convertObject(obj.unwrap)
      case _ => throw new IllegalArgumentException(s"Could not convert value to a double [${result.getClass}]")

    }
  }

  case class ToDate(expression: Expression) extends Expression {
    def perform(table: Table, row: Row): Object = {
      convertObject(expression.perform(table, row))
    }

    protected def convertObject(result: Object): LocalDate = result match {
      case int: java.lang.Integer => convertObject(new java.lang.Long(int.toLong))
      case long: java.lang.Long => LocalDate.ofEpochDay(long)
      case string: String => LocalDate.parse(string)
      case obj: TObjectValue => convertObject(obj.unwrap)
      case date: LocalDate => date
      case _ => throw new IllegalArgumentException(s"Could not convert value to a date [${result.getClass}]")
    }
  }
}
