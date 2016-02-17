package com.tspowell.grid.model

case class TProject(table: TTable,
                   rename: Map[String, String] = Map(),
                   include: Set[String] = Set(),
                   exclude: Set[String] = Set()) extends TObject