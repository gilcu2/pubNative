
name := "pubNtive"

version := "0.1"

scalaVersion := "2.12.12"

val sparkV = "2.4.5"
val circeVersion = "0.12.3"

libraryDependencies ++= Seq(

	"io.circe" %% "circe-core" % circeVersion,
	"io.circe" %% "circe-generic" % circeVersion,
	"io.circe" %% "circe-parser" % circeVersion,

	"org.apache.spark" %% "spark-sql" % sparkV % "provided",

	"org.scalatest" %% "scalatest" % "3.2.0" % "test"

)

mainClass in(Compile, run) := Some("PubNativeMain")

test in assembly := {}

assemblyJarName in assembly := "PubNative.jar"

assemblyMergeStrategy in assembly := {
	//  case PathList("org", "apache", "spark", "unused", "UnusedStubClass.class") => MergeStrategy.first
	case PathList("META-INF", xs@_*) => MergeStrategy.discard
	case x => MergeStrategy.first
}

assemblyShadeRules in assembly := Seq(
	ShadeRule.rename("org.slf4j.**" -> "shaded.@1").inAll
)
