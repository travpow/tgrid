package com.tspowell.grid.model

import java.time.{LocalDate, Month}

import com.tspowell.grid.builtins.Today
import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.TObject._
import com.tspowell.grid.model.column._
import junit.framework.TestCase
import org.junit.Assert

class TableTests extends TestCase {

  def testTableConstruction() {
    val table = Table("TestTable",
      Column("Name",       classOf[String], WithDefault("Default value")),
      Column("Age",        classOf[Int],    WithDefault(123)),
      Column("Birth Date", classOf[LocalDate])
    )

    Assert.assertEquals(table.size, 0)
    Assert.assertEquals(table.getColumns.length, 3)
    Assert.assertEquals(table.getColumns(0).name, "Name")
    Assert.assertEquals(table.getColumns(0).typeClass, classOf[String])
    Assert.assertEquals(table.getColumns(1).name, "Age")
    Assert.assertEquals(table.getColumns(1).typeClass, classOf[Int])
    Assert.assertEquals(table.getColumns(2).name, "Birth Date")
    Assert.assertEquals(table.getColumns(2).typeClass, classOf[LocalDate])

    table insert Row()
    Assert.assertEquals(table.size, 1)
    table insert Row("Real value", 100: Integer)
    Assert.assertEquals(table.size, 2)
    table insert Row("Missing number")
    Assert.assertEquals(table.size, 3)

    val rows = table.getRows

    Assert.assertEquals(s"Row(0) = ${rows(0)(0)}", rows(0)(0), "Default value")
    Assert.assertEquals(s"Row(1) = ${rows(0)(1)}", rows(0)(1).asInstanceOf[Integer], 123)
    Assert.assertEquals(s"Row(2) = ${rows(0)(2)}", rows(0)(2), null)
    Assert.assertEquals(s"Row(0) = ${rows(1)(0)}", rows(1)(0), "Real value")
    Assert.assertEquals(s"Row(1) = ${rows(1)(1)}", rows(1)(1).asInstanceOf[Integer], 100)
    Assert.assertEquals(s"Row(0) = ${rows(1)(0)}", rows(2)(0), "Missing number")
    Assert.assertEquals(s"Row(1) = ${rows(1)(1)}", rows(2)(1).asInstanceOf[Integer], 123)
  }

  def testProjection() {
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
      Column("Birth Date", classOf[LocalDate]),
      Column("One",        classOf[Int], WithDefault(1)),
      Column("Two",        classOf[Int], WithDefault(2)),
      Column("AddExpr",    classOf[Int], Add("One", "Two")))

    val addExprIndex = table2.getColumnIndex("AddExpr")
    assert(addExprIndex >= 0)
    val addExpr = table2.getColumns(addExprIndex).expression
    assert(addExpr.isDefined)

    // Test table calculation
    val row = Row("Travis", LocalDate.of(2000, Month.JANUARY, 1), 2: Integer, 4: Integer)
    Assert.assertEquals(addExpr.get.perform(table2, row).asInstanceOf[Double].toInt, 6)

    print(table2)
  }

  def testWhere(): Unit = {
    val table = Table("TestTable",
      Column("Name",       classOf[String], WithDefault("Default value")),
      Column("Age",        classOf[Int],    WithDefault(123)),
      Column("Birth Date", classOf[LocalDate])
    )

    table insert Row()
    table insert Row("Real value", 100: Integer)
    table insert Row("Missing number")

    val table2 = table.where(r => r.getValues(1).asInstanceOf[Integer] == 100)
    Assert.assertEquals(table2.size, 1)
    Assert.assertEquals(table2.getRows(0)(0), "Real value")
    Assert.assertEquals(table2.getRows(0)(1).asInstanceOf[Integer], 100)
    Assert.assertEquals("Should not have modified original Table", table.size, 3)
  }

  def testDependency(): Unit = {
    val table = Table("TestTable",
      Column("Name",        classOf[String], WithDefault("John Doe")),
      Column("Birth Date",  classOf[LocalDate])
    )

    class MockToday() extends Today {
      override def perform(table: Table, row: Row): Object = {
        LocalDate.of(2001, Month.JANUARY, 1)
      }
    }

    val dependentTable = Table("DepTable", table)
    val derivedTable = Table("DerivedTable", dependentTable,
      Column("Age", classOf[Double], Subtract(new MockToday(), "Birth Date")))

    table insert Row("Travis", LocalDate.of(2000, Month.JANUARY, 1))

    Assert.assertEquals(dependentTable.getRows(0)(0), "Travis")
    val derivedRows = derivedTable.getRows
    Assert.assertEquals(derivedRows.length, 1)
    Assert.assertEquals(derivedRows(0).length, 3)

    val ageInSeconds = derivedRows(0)(2).asInstanceOf[Double].toInt
    Assert.assertEquals(ageInSeconds, 31622400 )
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

    Assert.assertEquals(table.size, 2 * MAX)
    Assert.assertEquals(table.where(x => x.cellValues.head.unwrap == "First").size, MAX)
  }
}