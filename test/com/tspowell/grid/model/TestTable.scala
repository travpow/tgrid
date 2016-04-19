package com.tspowell.grid.model

import java.util.Date

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.TObject._
import com.tspowell.grid.model.column.{Add, Column, WithDefault}

object TestTable {

  def test() {
    val table = Table("TestTable",
      Column("Name",       classOf[String], WithDefault("Default value")),
      Column("Age",        classOf[Int],    WithDefault(123)),
      Column("Birth Date", classOf[Date])
    )

    assert(table.size == 0)
    assert(table.getColumns.length == 3)
    assert(table.getColumns(0).name == "Name")
    assert(table.getColumns(0).typeClass == classOf[String])
    assert(table.getColumns(1).name == "Age")
    assert(table.getColumns(1).typeClass == classOf[Int])
    assert(table.getColumns(2).name == "Birth Date")
    assert(table.getColumns(2).typeClass == classOf[Date])

    table insert Row()
    assert(table.size == 1)
    table insert Row("Real value", 100: Integer)
    assert(table.size == 2)
    table insert Row("Missing number")
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
    val table = Table("TestTable",
      Column("Name", classOf[String], WithDefault("Default value")),
      Column("Age",  classOf[Int],    WithDefault(123))
    )

    val projectedTable = Table("NewTable",
      Projection(table,
        rename = Map("Name" -> "Full Name"),
        exclude = Set("Age"))
    )

    val table2 = Table("Test2Table",
      projectedTable,
      Column("Birth Date", classOf[Date]), 
      Column("One",        classOf[Int], WithDefault(1)),
      Column("Two",        classOf[Int], WithDefault(2)),
      Column("AddExpr",    classOf[Int], Add("One", "Two")))

    val addExprIndex = table2.getColumnIndex("AddExpr")
    assert(addExprIndex >= 0)
    val addExpr = table2.getColumns(addExprIndex).expression
    assert(addExpr.isDefined)

    // Test table calculation
    val row = Row("Travis", new Date(2000, 1, 1), 2: Integer, 4: Integer)
    assert(addExpr.get.perform(table2, row) == 6)

    print(table2)
  }

  def testWhere(): Unit = {
    val table = Table("TestTable",
      Column("Name",       classOf[String], WithDefault("Default value")),
      Column("Age",        classOf[Int],    WithDefault(123)),
      Column("Birth Date", classOf[Date])
    )

    table insert Row()
    table insert Row("Real value", 100: Integer)
    table insert Row("Missing number")

    val table2 = table.where(r => r.getValues(1) == 100)
    assert(table2.size == 1)
    assert(table2.getRows(0)(0) == "Real value")
    assert(table2.getRows(0)(1) == 100)
    assert(table.size == 3, "Should not have modified original Table")
  }
}

object Main extends App {
  override def main(args: Array[String]): Unit = {
    List[() => Unit](
      TestTable.test,
      TestTable.test2,
      TestTable.testWhere
    ).foreach(_())

    println("All tests passed.")
  }
}
