package com.tspowell.grid.model

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.column.{Expression, Column}

import scala.collection.mutable

case class Table(name: String, parameters: TObject*) extends TObject {
  private val columns = new mutable.LinkedHashMap[String, Column[_]]
  private val rows = new mutable.ArrayBuffer[Row]
  private val dependencies = new mutable.LinkedHashSet[Table]
  private val dataSources = new mutable.LinkedHashSet[TDataSource]
  private val listeners = new mutable.HashSet[TableListener]()

  private lazy val defaultRow = Row(columns.values.map(_.default.orNull).toSeq: _*)
  private lazy val columnsByIndex = columns.keys.toArray

  parameters.toList.foreach(addParameterization)

  def addDependency(dependent: Table): Unit = {
    dependencies += dependent
  }

  def insert(row: Row): Unit = {
    if (dataSources.nonEmpty) {
      throw new RuntimeException("Not allowed to insert data into a table listening to external data sources.")
    }

    val calculated = fillIn(row)
    rows += calculated

    dependencies.foreach(_ insert calculated)
  }

  def getColumns: Array[Column[_]] = columns.values.toArray
  def getColumnNames: Seq[String] = columns.keys.toSeq
  def getColumnIndex(name: String): Int = columnsByIndex.indexOf(name)
  def size: Int = rows.length
  def getRows: Array[Array[Object]] = rows.toArray.map(_.getValues)

  def where(predicate: (Row) => Boolean): Table = {
    new Table(this.name, rows.filter(predicate):_*)
  }

  private def fillIn(row: Row): Row = {
    Option(row).map {
      _.orSetValue(this, columns.toIndexedSeq)
    }.getOrElse(defaultRow)
  }

  private def addParameterization(obj: TObject): Unit = obj match {
    case row: Row =>
      rows.append(row)
    case column: Column[_] =>
      column.forTable(this)
      columns(column.name) = column
    case table: Table =>
      table.addDependency(this)
      table.getColumns.foreach { otherColumn =>
        println(s"Adding column from table [${table.name}]: $otherColumn")
        columns(otherColumn.name) = otherColumn
      }
    case TDataSource(dataSourceName) =>
      println(s"Populating $name from $dataSourceName")
    case projection: Projection =>
      projection.table.addDependency(this)
      projection.table.getColumns.foreach { otherColumn =>
        val shouldAddColumn = !projection.exclude.contains(otherColumn.name) &&
          (projection.include.isEmpty || projection.include.contains(otherColumn.name))

        if (shouldAddColumn) {
          val currentName = otherColumn.name
          val newName = projection.rename.getOrElse(currentName, currentName)
          val newColumn = Column(newName, otherColumn.typeClass, otherColumn.definitions: _*)
          columns(projection.rename(currentName)) = newColumn
        }
      }
    case _ =>
      throw new UnsupportedOperationException(s"Could not apply parameterization of type [${obj.getClass}] to table: " + obj)
  }
}
