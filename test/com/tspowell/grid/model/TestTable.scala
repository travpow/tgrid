package com.tspowell.grid.model

import java.time.Duration
import java.util.Date

import com.tspowell.grid.builtins.Today
import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.TObject._
import com.tspowell.grid.model.column._

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

    assert(rows(0)(0) == "Default value",           s"Row(0) = ${rows(0)(0)}")
    assert(rows(0)(1).asInstanceOf[Integer] == 123, s"Row(1) = ${rows(0)(1)}")
    assert(rows(0)(2) == null,                      s"Row(2) = ${rows(0)(2)}")
    assert(rows(1)(0) == "Real value",              s"Row(0) = ${rows(1)(0)}")
    assert(rows(1)(1).asInstanceOf[Integer] == 100, s"Row(1) = ${rows(1)(1)}")
    assert(rows(2)(0) == "Missing number",          s"Row(0) = ${rows(1)(0)}")
    assert(rows(2)(1).asInstanceOf[Integer] == 123, s"Row(1) = ${rows(1)(1)}")
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
    assert(addExpr.get.perform(table2, row).asInstanceOf[Double].toInt == 6)

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

    val table2 = table.where(r => r.getValues(1).asInstanceOf[Integer] == 100)
    assert(table2.size == 1)
    assert(table2.getRows(0)(0) == "Real value")
    assert(table2.getRows(0)(1).asInstanceOf[Integer] == 100)
    assert(table.size == 3, "Should not have modified original Table")
  }

  def testDependency(): Unit = {
    val table = Table("TestTable",
      Column("Name",        classOf[String], WithDefault("John Doe")),
      Column("Birth Date",  classOf[Date])
    )

    class MockToday() extends Today {
      override def perform(table: Table, row: Row): Object = {
        new Date(2001,1,1)
      }
    }

    val dependentTable = Table("DepTable", table)
    val derivedTable = Table("DerivedTable", dependentTable,
      Column("Age", classOf[Double], Subtract(new MockToday(), "Birth Date")))

    table insert Row("Travis", new Date(2000, 1, 1))

    assert(dependentTable.getRows(0)(0) == "Travis")
    val derivedRows = derivedTable.getRows
    assert(derivedRows.length == 1)
    assert(derivedRows(0).length == 3)

    val ageInSeconds = derivedRows(0)(2).asInstanceOf[Double].toInt
    assert(Duration.ofSeconds(ageInSeconds).toDays == 365 /* days */)
  }

  def testInsertManyRows(): Unit = {
    val table = Table("Big Table",
      Column("String", classOf[String]),
      Column("Double", classOf[Double], WithDefault(0)))

    val calcTable = Table("Calc'ed Table",
      table,
      Column("Increment", classOf[Int], Add("Double", L(1: Integer))))

    println("Inserting 100k rows...")

    val MAX = 100000

    for (i <- 0 until MAX) {
      table insert Row("First")
      table insert Row("Second", i: Integer)
    }

    println("All rows inserted.")

    assert(table.size == 2 * MAX)
    assert(table.where(x => x.cellValues.head.unwrap == "First").size == MAX)
  }
}

object Main extends App {
  override def main(args: Array[String]): Unit = {
    List[() => Unit](
      TestTable.test,
      TestTable.test2,
      TestTable.testWhere,
      TestTable.testDependency,
      TestTable.testInsertManyRows
    ).foreach(_())

    println("All tests passed.")
  }
}
