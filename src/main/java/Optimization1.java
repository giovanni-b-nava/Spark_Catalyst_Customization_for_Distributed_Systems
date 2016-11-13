import JsonParser.ParserJson;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;

import java.util.List;


/**
 * Created by Spark on 11/11/2016.
 */
public class Optimization1 {

    public static void main(String[] args) {

        SparkSession sparkSession = SparkSession
                .builder()
                .master("local")
                .appName("Java Spark SQL Example")
                .getOrCreate();

        // Generation of the datasets
        Dataset<Row> employees = sparkSession.read().option("header","true").csv("src/main/resources/employees.csv");
        Dataset<Row> salaries = sparkSession.read().option("header","true").csv("src/main/resources/salaries.csv");
        Dataset<Row> departments = sparkSession.read().option("header","true").csv("src/main/resources/departments.csv");
        Dataset<Row> titles = sparkSession.read().option("header","true").csv("src/main/resources/titles.csv");
        Dataset<Row> dept_emp = sparkSession.read().option("header","true").csv("src/main/resources/dept_emp.csv");
        Dataset<Row> dept_manager = sparkSession.read().option("header","true").csv("src/main/resources/dept_manager.csv");

        // Prova file configurazione
        Configurator conf = new Configurator();
        conf.Builder();
        List<Node> nodi = conf.nodes;
        System.out.println(nodi.get(1).getName());

        // Create views
        salaries.createOrReplaceTempView("salaries");
        employees.createOrReplaceTempView("employees");
        titles.createOrReplaceTempView("titles");

        // Query
        Dataset<Row> sqlDF = sparkSession.sql("SELECT first_name FROM salaries s Join employees e ON s.emp_no=e.emp_no WHERE salary>70000 GROUP BY first_name");
        System.out.println(sqlDF.queryExecution().optimizedPlan().numberedTreeString());

        // produce l'albero delle varie fasi di ottimizzazione
        System.out.println(sqlDF.queryExecution());

        // Prova parser
        ParserJson p = new ParserJson();
        System.out.println(p.parse("{\"data\": { \"translations\": [ { \"translatedText\": \"Hello world\"}]}}"));

        // Generazione strutture dati dell'albero
        LogicalPlan plan = sqlDF.queryExecution().optimizedPlan();
        Collector c = new Collector();
        c.collect(plan);
        System.out.println(c.operations.toString());
    }
}
