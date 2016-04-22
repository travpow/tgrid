package com.tspowell.grid.util

import java.util.Date

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.column.Expression
import com.tspowell.grid.model.{Table, TObjectValue}

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
      case date: Date => date.toInstant.getEpochSecond.toDouble
      case obj: TObjectValue => convertObject(obj.unwrap)
      case _ => throw new IllegalArgumentException(s"Could not convert value to a double [${result.getClass}]")

    }
  }

  case class ToDate(expression: Expression) extends Expression {
    def perform(table: Table, row: Row): Object = {
      convertObject(expression.perform(table, row))
    }

    protected def convertObject(result: Object): Date = result match {
      case int: java.lang.Integer => convertObject(new java.lang.Long(int.toLong))
      case long: java.lang.Long => new Date(long)
      case string: String => new Date(string)
      case obj: TObjectValue => convertObject(obj.unwrap)
      case date: Date => date
      case _ => throw new IllegalArgumentException(s"Could not convert value to a date [${result.getClass}]")
    }
  }
}
