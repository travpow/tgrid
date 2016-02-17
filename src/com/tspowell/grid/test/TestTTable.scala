package com.tspowell.grid.test

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

import com.tspowell.grid.containers.TRow
import com.tspowell.grid.model.TObject._
import com.tspowell.grid.model.column.{Add, TColumn, WithDefault}
import com.tspowell.grid.model.{TProject, TTable}

object TestTTable {

  def test() {
    val table = TTable("TestTable",
      TColumn("Name",       classOf[String], WithDefault("Default value")),
      TColumn("Age",        classOf[Int],    WithDefault(123)),
      TColumn("Birth Date", classOf[Date])
    )

    assert(table.size == 0)
    assert(table.getColumns.length == 3)
    assert(table.getColumns(0).name == "Name")
    assert(table.getColumns(0).typeClass == classOf[String])
    assert(table.getColumns(1).name == "Age")
    assert(table.getColumns(1).typeClass == classOf[Int])
    assert(table.getColumns(2).name == "Birth Date")
    assert(table.getColumns(2).typeClass == classOf[Date])

    table insert TRow()
    assert(table.size == 1)
    table insert TRow("Real value", 100: Integer)
    assert(table.size == 2)
    table insert TRow("Missing number")
    assert(table.size == 3)

    val rows = table.getRows

    assert(rows(0)(0) == "Default value",  s"Row(0) = ${rows(0)(0)}")
    assert(rows(0)(1) == 123,              s"Row(1) = ${rows(0)(1)}")
    assert(rows(0)(2) == null,             s"Row(2) = ${rows(0)(2)}")
    assert(rows(1)(0) == "Real value",     s"Row(0) = ${rows(1)(0)}")
    assert(rows(1)(1) == 100,              s"Row(1) = ${rows(1)(1)}")
    assert(rows(2)(0) == "Missing number", s"Row(0) = ${rows(1)(0)}")
    assert(rows(2)(1) == 123,              s"Row(1) = ${rows(1)(1)}")
  }

  def test2() {
    val table = TTable("TestTable",
      TColumn("Name", classOf[String], WithDefault("Default value")),
      TColumn("Age", classOf[Int], WithDefault(123))
    )

    val projectedTable = TTable("NewTable",
      TProject(table,
        rename = Map("Name" -> "Full Name"),
        exclude = Set("Age"))
    )

    val table2 = TTable("Test2Table",
      projectedTable,
      TColumn("Birth Date", classOf[Date]),
      TColumn("One",        classOf[Int], WithDefault(1)),
      TColumn("Two",        classOf[Int], WithDefault(2)),
      TColumn("AddExpr",    classOf[Int], Add("One", "Two")))

    val addExprIndex = table2.getColumnIndex("AddExpr")
    assert(addExprIndex >= 0)
    val addExpr = table2.getColumns(addExprIndex).expression
    assert(addExpr.isDefined)

    // Test table calculation
    val row = TRow("Travis", new Date(2000, 1, 1), 2: Integer, 4: Integer)
    assert(addExpr.get.perform(table2, row) == 6)

    print(table2)
  }
}

object Main extends App {
  override def main(args: Array[String]): Unit = {
    List[() => Unit](
      TestTTable.test,
      TestTTable.test2
    ).foreach(_())

    println("All tests passed.")
  }
}
