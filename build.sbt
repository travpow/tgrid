name := "tgrid"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "net.debasishg" %% "redisclient" % "3.0",
  "org.scala-lang.modules" %% "scala-pickling" % "0.10.1",
  "junit" % "junit" % "4.11" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test
    exclude("junit", "junit-dep")
)