
package com.tspowell.grid.containers

import scala.collection.mutable

object TGrid {
  def apply: TGrid = new TGrid
}

class TGrid {
  private val buffer = new mutable.ArrayBuffer[Row]

  def append(rows: Row*): Unit = {
    buffer.appendAll(rows.toList)
  }
}