package com.tspowell.grid.model.column

import com.tspowell.grid.model.{TObject, TObjectValue, TTable}

import scala.collection.mutable

trait ColumnOp

case class TColumn[T](name: String, typeClass: Class[T], definitions: ColumnOp*) extends TObject {
  private val tables = new mutable.HashSet[TTable]

  val default = definitions
    .filter(_.isInstanceOf[WithDefault[_]])
    .map(_.asInstanceOf[WithDefault[_]])
    .map {
      case WithDefault(defaultValue) => TObjectValue(defaultValue.asInstanceOf[Object])
    }.lastOption

  val expression = definitions
    .filter(_.isInstanceOf[Expression])
    .map(_.asInstanceOf[Expression]).lastOption

  def forTable(table: TTable): Unit = {
    tables += table
  }
}