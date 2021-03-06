package com.tspowell.grid.builtins

import java.lang.Double

import com.tspowell.grid.containers.Row
import com.tspowell.grid.model.Table
import com.tspowell.grid.model.column.Column
import junit.framework.TestCase
import org.junit.Assert

class MathTests extends TestCase {
  def testBasicMathOps(): Unit = {
    val table = Table("Bad Division",
      Column("LHS", classOf[Double]),
      Column("RHS", classOf[Double]),
      Column("Div", classOf[Double], Div("LHS", "RHS")),
      Column("Mul", classOf[Double], Mul("LHS", "RHS")),
      Column("Max", classOf[Double], Max("LHS", "RHS")))

    List[(Double, Double)](
      (0.0, 1.0),
      (3.0, 4.0),
      (5.0, 0.0)
    ).foreach { case (one, two) =>
      table insert Row(one, two)
    }

    val rows = table.getRows
    Assert.assertEquals(3,          rows.length)

    Assert.assertEquals(0.0,        rows(0)(2))
    Assert.assertEquals(0.0,        rows(0)(3))
    Assert.assertEquals(1.0,        rows(0)(4))

    Assert.assertEquals(0.75,       rows(1)(2))
    Assert.assertEquals(12.0,       rows(1)(3))
    Assert.assertEquals(4.0,        rows(1)(4))

    Assert.assertEquals(Double.NaN, rows(2)(2))
    Assert.assertEquals(0.0,        rows(2)(3))
    Assert.assertEquals(5.0,        rows(2)(4))

  }
}
