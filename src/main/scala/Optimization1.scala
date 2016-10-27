import scala.util.parsing.json._
import scala.io.Source
import org.apache.spark.sql.SparkSession
/**
  * Created by Spark on 27/10/2016.
  */
object Optimization1 {

  def main(args: Array[String]): Unit =
  {
    val sparkSession = SparkSession.builder.
      master("local")
      .appName("optimization1")
      .getOrCreate()

    val employees = sparkSession.read.json("src/main/resources/Employees.json")
    val salaries = sparkSession.read.json("src/main/resources/Salaries.json")
    val departments = sparkSession.read.json("src/main/resources/Departments.json")
    val titles = sparkSession.read.json("src/main/resources/Titles.json")
    val dept_emp = sparkSession.read.json("src/main/resources/Dept_emp.json")
    val dept_manager = sparkSession.read.json("src/main/resources/Dept_manager.json")

    // Parsing da file a formato map di string
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
