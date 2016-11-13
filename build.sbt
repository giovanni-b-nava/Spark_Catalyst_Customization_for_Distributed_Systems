name := "Spark Catalyst Customization for Distributed Systems"

version := "1.0"

scalaVersion := "2.10.4"

val sparkVersion = "2.1.0-SNAPSHOT"


resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/",
  "gson" at "http://repositories/releases/com/google/code/gson/gson/"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-hive" % sparkVersion,
  "com.google.code.gson" % "gson" % "2.8.0"
)


