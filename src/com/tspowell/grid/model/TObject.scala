package com.tspowell.grid.model

trait TObject extends Object {
  def unwrap: Object = {
    println("Unwrapping as generic TObject.")
    this
  }
}

case class TObjectValue(value: Object) extends TObject {
  override def unwrap: Object = {
    value
  }
}

// Internal TObject type mappings

object TObject {
  implicit def object2TObject(value: String): TObject            = TObjectValue(value)
  implicit def object2TObject(value: java.lang.Integer): TObject = TObjectValue(value)
  implicit def object2TObject(value: java.time.LocalDate): TObject    = TObjectValue(value)
  implicit def object2TObject(value: Object): TObject            = TObjectValue(value)
}
