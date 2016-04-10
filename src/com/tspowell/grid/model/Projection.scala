package com.tspowell.grid.model

case class Projection(table: Table,
                      rename: Map[String, String] = Map(),
                      include: Set[String] = Set(),
                      exclude: Set[String] = Set()) extends TObject