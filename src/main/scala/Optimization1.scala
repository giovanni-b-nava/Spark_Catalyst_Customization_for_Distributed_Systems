import scala.util.parsing.json._
import scala.io.Source
/**
  * Created by Spark on 27/10/2016.
  */
object Optimization1 {

  def main(args: Array[String]): Unit =
  {
    /*
    val sparkSession = SparkSession.builder.
      master("optimization")
      .appName("rule1")
      .getOrCreate()

    val dataFrame = sparkSession.read.json("src/main/resources/DB_Employees.json")
    */

    val jsonSource : String = Source.fromFile("src/main/resources/DB_config.json").getLines.mkString

    val result = JSON.parseFull(jsonSource)
    result match {
      // Matches if jsonStr is valid JSON and represents a Map of Strings to Any
      case Some(map: Map[String, Any]) => println(map)
      case None => println("Parsing failed")
      case other => println("Unknown data structure: " + other)
    }

  }
}
