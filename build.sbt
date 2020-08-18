name := "pubNtive"

version := "0.1"

scalaVersion := "2.12.12"

val sparkV = "2.4.5"
val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
	"io.circe" %% "circe-core",
	"io.circe" %% "circe-generic",
	"io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= Seq(

	"org.apache.spark" %% "spark-sql" % sparkV % "provided",

	"org.scalatest" %% "scalatest" % "3.2.0" % "test"

)