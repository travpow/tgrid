package com.tspowell.grid.model

import com.tspowell.grid.containers.TRow
import com.tspowell.grid.model.column.{ColumnOp, TColumn}

import scala.collection.mutable

case class TTable(name: String, parameters: TObject*) extends TObject {
  private val columns = new mutable.LinkedHashMap[String, TColumn[_]]
  private val rows = new mutable.ArrayBuffer[TRow]
  private val dependencies = new mutable.LinkedHashSet[TTable]
  private val dataSources = new mutable.LinkedHashSet[TDataSource]
  private val listeners = new mutable.HashSet[TableListener]

  private lazy val defaultRow = TRow(columns.values.map(_.default.orNull).toSeq: _*)
  private lazy val columnsByIndex = columns.keys.toArray

  parameters.toList.foreach(addParameterization)

  def addDependency(dependent: TTable): Unit = {
    dependencies += dependent
  }

  def insert(row: TRow): Unit = {
    if (dataSources.nonEmpty) {
      throw new RuntimeException("Not allowed to insert data into a table listening to external data sources.")
    }

    rows += fillIn(row)
  }

  def getColumns: Array[TColumn[_]] = columns.values.toArray
  def getColumnNames: Seq[String] = columns.keys.toSeq
  def getColumnIndex(name: String): Int = columnsByIndex.indexOf(name)
  def size: Int = rows.length
  def getRows: Array[Array[Object]] = rows.toArray.map(_.getValues)

  private def fillIn(row: TRow): TRow = {
    Option(row).map {
      _.orSetDefaults(columns.toMap, columnsByIndex.toIndexedSeq)
    }.getOrElse(defaultRow)
  }

  private def addParameterization(obj: TObject): Unit = obj match {
    case column: TColumn[_] =>
      column.forTable(this)
      columns(column.name) = column
    case table: TTable =>
      table.addDependency(this)
      table.getColumns.foreach { otherColumn =>
        println(s"Adding column from table [${table.name}]: $otherColumn")
        columns(otherColumn.name) = otherColumn
      }
    case TDataSource(dataSourceName) =>
      println(s"Populating $name from $dataSourceName")
    case projection: TProject =>
      projection.table.addDependency(this)
      projection.table.getColumns.foreach { otherColumn =>
        val shouldAddColumn = !projection.exclude.contains(otherColumn.name) &&
          (projection.include.isEmpty || projection.include.contains(otherColumn.name))

        if (shouldAddColumn) {
          val currentName = otherColumn.name
          val newName = projection.rename.getOrElse(currentName, currentName)
          val newColumn = TColumn(newName, otherColumn.typeClass, otherColumn.definitions: _*)
          columns(projection.rename(currentName)) = newColumn
        }
      }
    case _ =>
      throw new UnsupportedOperationException(s"Could not apply parameterization of type [${obj.getClass}] to table.")
  }
}
