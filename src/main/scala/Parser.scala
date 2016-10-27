import scala.io.Source
import scala.util.parsing.json.JSON

/**
  * Created by Spark on 19/10/2016.
  */
class CC[T] { def unapply(a:Any):Option[T] = Some(a.asInstanceOf[T]) }

object Name extends CC[String]
object Type extends CC[String]
object Performance extends CC[Map[String, Double]]
object Cost extends CC[Map[String, Double]]
object Link extends CC[Array[Map[String, Double]]]
object Table extends CC[Array[Map[String, Any]]]

object M extends CC[Map[String, Any]]
object L extends CC[List[Any]]
object S extends CC[String]
object D extends CC[Double]
object B extends CC[Boolean]


/*val jsonSource : String = Source.fromFile("src/main/resources/DB_config.json").getLines.mkString

val result = for {
  Some(M(map)) <- List(JSON.parseFull(jsonSource))
  L(languages) = map("languages")
  M(language) <- languages
  S(name) = language("name")
  B(active) = language("is_active")
  D(completeness) = language("completeness")
} yield {
  (name, active, completeness)
}

assert( result == List(("English",true,2.5), ("Latin",false,0.9)))
{

}*/
