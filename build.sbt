name := "scala-chart"

version := "1.0"

scalaVersion := "2.12.3"

libraryDependencies += "org.apache.commons" % "commons-csv" % "1.4"
// https://mvnrepository.com/artifact/mysql/mysql-connector-java

// https://mvnrepository.com/artifact/jfree/jfreechart
libraryDependencies += "jfree" % "jfreechart" % "1.0.13"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % "2.12.3"
)
