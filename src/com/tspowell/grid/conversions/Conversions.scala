package com.tspowell.grid.conversions

trait Conversion[T] {
  def convert(result: Object): T
}
