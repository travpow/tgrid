package com.tspowell.grid.builtins

import java.time.{Month, LocalDate}

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.Table
import com.tspowell.grid.model.column.{Column, L}
import junit.framework.TestCase
import org.junit.Assert

class FunctionsTest extends TestCase {
  def testStr = {
    val table = Table("String concat",
      Column("Stringified number", classOf[String], Str(L(1: Integer))),
      Column("Concat strings", classOf[String], Str(L("one"), L("two"))),
      Column("Duration", classOf[String], Str(L("Took: "),
        Subtract(L(LocalDate.of(2001, Month.JANUARY, 1)), L(LocalDate.of(2000,Month.JANUARY, 1))))),
      Column("Date", classOf[String], Str(L("On this day: "), L(LocalDate.of(2000, Month.JANUARY, 1))))
    )

    table insert Row()

    val row: Array[Object] = table.getRows(0).map(_.asInstanceOf[String])
    val expected = Array[Object]("1", "onetwo", "Took: 366 days", "On this day: 2000-01-01")

    Assert.assertArrayEquals(expected, row)
  }
}
