package com.tspowell.grid.model.column

import scala.collection.immutable.HashMap

case class WithDefault[T](default: T) extends ColumnOp
case class WithFormatting() extends HashMap[String, Object] with ColumnOp {

}