package com.tspowell.grid.server

object TGridServer {
  private val ConfigPrefix = "--config="

  def main (args: Array[String]): Unit = {
    val configFile = args
      .filter(_.startsWith(ConfigPrefix))
      .map(_.stripPrefix(ConfigPrefix))
      .lastOption

    configFile match {
      case None => throw new Exception(s"No configuration file passed: use '$ConfigPrefix'")
      case Some(file) => println("ok")//new TGridServer.start(file)
    }
  }
}

class TGridServer {
  def start(fileName: String): Unit = {

  }
}

