import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.expressions.{Literal, Multiply}
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan
import org.apache.spark.sql.catalyst.rules.Rule
/**
  * Created by Spark on 27/10/2016.
  */
object Optimization1 {

  def main(args: Array[String]): Unit = {

    val sparkSession = SparkSession.builder.
      master("optimization")
      .appName("rule1")
      .getOrCreate()

    val df = sparkSession.read.json("src/main/resources/DB_Employees.json")

  }
}
