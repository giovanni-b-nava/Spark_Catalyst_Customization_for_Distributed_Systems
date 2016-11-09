import scala.io.Source
import scala.util.parsing.json.JSON

/**
  * Created by Spark on 19/10/2016.
  */
/*class Node(
  val name: String,
  val category: String,
  val performance: List[Map[String, Double]],
  val costs: List[Map[String,Double]],
  val links: List[List[Map[String,Double]]],
  val tables: List[Map[String, Any]]
  ) {}*/


object Parser {

  /*def main(args: Array[String]): Unit = {
    val jsonSource: String = Source.fromFile("src/main/resources/DB_config.json").getLines.mkString

    val json: Option[Any] = JSON.parseFull(jsonSource)
    val map: Map[String, Any] = json.get.asInstanceOf[Map[String, Any]]
    val nodes: List[Any] = map.get("nodes").get.asInstanceOf[List[Any]]
    nodes.foreach(langMap => {
      val node: Map[String, Any] = langMap.asInstanceOf[Map[String, Any]]
      val name: String = node.get("name").get.asInstanceOf[String]
      val category: String = node.get("category").get.asInstanceOf[String]
      val performance: List[Map[String,Double]] = node.get("performance").get.asInstanceOf[List[Map[String,Double]]]
      //var n : Node = new Node()
      /* val isActive:Boolean = language.get("is_active").get.asInstanceOf[Boolean]
  val completeness:Double = language.get("completeness").get.asInstanceOf[Double]*/
    })

    println(nodes)

  } */
}