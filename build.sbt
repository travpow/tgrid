name := "tgrid"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "net.debasishg" %% "redisclient" % "3.0",
  "org.scala-lang.modules" %% "scala-pickling" % "0.10.1",
  "junit" % "junit" % "4.11" % Test,
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "com.novocode" % "junit-interface" % "0.11" % Test
    exclude("junit", "junit-dep")
)