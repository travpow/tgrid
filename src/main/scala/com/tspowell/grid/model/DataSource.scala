package com.tspowell.grid.model

import com.tspowell.grid.containers.Row

trait DataSource extends TObject {
  /**
    * Transmit changes from a row back to a data source
    */
  def propogateChanges(row: Row, index: Integer): Unit
  def name: String
}
