import scala.util.parsing.json._
import scala.io.Source
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.expressions.{Literal, Multiply}
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.catalyst.rules.Rule
/**
  * Created by Spark on 27/10/2016.
  */
object Optimization1 {

  def main(args: Array[String])  : Unit = {

    val sparkSession = SparkSession.builder.
      master("local")
      .appName("optimization1")
      .getOrCreate()

    val employees = sparkSession.read.option("header","true").csv("src/main/resources/employees.csv")
    val salaries = sparkSession.read.option("header","true").csv("src/main/resources/salaries.csv")
    val departments = sparkSession.read.option("header","true").csv("src/main/resources/departments.csv")
    val titles = sparkSession.read.option("header","true").csv("src/main/resources/titles.csv")
    val dept_emp = sparkSession.read.option("header","true").csv("src/main/resources/dept_emp.csv")
    val dept_manager = sparkSession.read.option("header","true").csv("src/main/resources/dept_manager.csv")

   /* // Parsing da file a formato map di string
    val jsonSource : String = Source.fromFile("src/main/resources/DB_config.json").getLines.mkString

    val result = JSON.parseFull(jsonSource)
    result match {
      // Matches if jsonStr is valid JSON and represents a Map of Strings to Any
      case Some(map: Map[String, Any]) => println(map)
      case None => println("Parsing failed")
      case other => println("Unknown data structure: " + other)
    }

    val path = "src/main/resources/salaries.csv"
    val db = sparkSession.read.option("header","true").csv(path)
    db.printSchema()*/

    /*val multipliedDF = employees.selectExpr("first_name")
      .join(salaries, employees ("emp_no") === salaries ("emp_no"))
      .where( employees ("gender") === "F")*/

    val conf = new Configurator
    conf.Builder()
    val nodi = conf.nodes
    println(nodi.get(1).getName)

    salaries.createOrReplaceTempView("salaries")
    employees.createOrReplaceTempView("employees")
    titles.createOrReplaceTempView("titles")
    val sqlDF = sparkSession.sql("SELECT first_name FROM salaries s Join employees e ON s.emp_no=e.emp_no WHERE gender=='F'")
    println(sqlDF.queryExecution.optimizedPlan.numberedTreeString)
    val sqlDF2 = sparkSession.sql("SELECT first_name FROM employees e JOIN (SELECT s.emp_no, salary FROM salaries s JOIN titles t " +
      "ON s.emp_no=t.emp_no GROUP BY s.salary HAVING t.title='Staff') st ON e.emp_no=st.emp_no GROUP BY first_name HAVING salary>70000")
    println(sqlDF2.queryExecution.optimizedPlan.numberedTreeString)

  }
}
