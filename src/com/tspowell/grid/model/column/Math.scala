package com.tspowell.grid.model.column

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.Table
import com.tspowell.grid.util.Conversions.ToDouble

/**
  * Created by travis on 4/19/16.
  */
case class Add(lhs: Expression, rhs: Expression) extends Expression {
  def perform(table: Table, row: Row): java.lang.Double = {
    val left = ToDouble(lhs)
    val leftP = left.perform(table, row)
    val right = ToDouble(rhs).perform(table, row)

    leftP.asInstanceOf[Double] + right.asInstanceOf[Double]
  }
}

case class Subtract(lhs: Expression, rhs: Expression) extends Expression {
  def perform(table: Table, row: Row): java.lang.Double = {
    val left = ToDouble(lhs).perform(table, row)
    val right = ToDouble(rhs).perform(table, row)

    left.asInstanceOf[Double] - right.asInstanceOf[Double]
  }
}
