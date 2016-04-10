package com.tspowell.grid.model.column

import com.tspowell.grid.model.{TObject, TObjectValue, Table}

import scala.collection.mutable

trait ColumnOp

case class Column[T](name: String, typeClass: Class[T], definitions: ColumnOp*) extends TObject {
  private val tables = new mutable.HashSet[Table]

  val default = definitions
    .filter(_.isInstanceOf[WithDefault[_]])
    .map(_.asInstanceOf[WithDefault[_]])
    .map {
      case WithDefault(defaultValue) => TObjectValue(defaultValue.asInstanceOf[Object])
    }.lastOption

  val expression = definitions
    .filter(_.isInstanceOf[Expression])
    .map(_.asInstanceOf[Expression]).lastOption

  def forTable(table: Table): Unit = {
    tables += table
  }
}