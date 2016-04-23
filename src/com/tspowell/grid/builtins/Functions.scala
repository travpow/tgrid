package com.tspowell.grid.builtins

import java.time.LocalDate

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.Table
import com.tspowell.grid.model.column.Expression

/**
  * Created by travis on 4/19/16.
  */
trait Functional extends Expression

case class Today() extends Functional {
  def perform(table: Table, row: Row): Object = {
    LocalDate.now()
  }
}

