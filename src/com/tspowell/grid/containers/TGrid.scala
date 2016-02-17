
package com.tspowell.grid.containers

import scala.collection.mutable

object TGrid {
  def apply: TGrid = new TGrid
}

class TGrid {
  private val buffer = new mutable.ArrayBuffer[TRow]

  def append(rows: TRow*): Unit = {
    buffer.appendAll(rows.toList)
  }
}