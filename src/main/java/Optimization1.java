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

        Dataset<Row> employees = sparkSession.read().option("header","true").csv("src/main/resources/employees.csv");
        Dataset<Row> salaries = sparkSession.read().option("header","true").csv("src/main/resources/salaries.csv");
        Dataset<Row> departments = sparkSession.read().option("header","true").csv("src/main/resources/departments.csv");
        Dataset<Row> titles = sparkSession.read().option("header","true").csv("src/main/resources/titles.csv");
        Dataset<Row> dept_emp = sparkSession.read().option("header","true").csv("src/main/resources/dept_emp.csv");
        Dataset<Row> dept_manager = sparkSession.read().option("header","true").csv("src/main/resources/dept_manager.csv");

        Configurator conf = new Configurator();
        conf.Builder();
        List<Node> nodi = conf.nodes;
        System.out.println(nodi.get(1).getName());

        salaries.createOrReplaceTempView("salaries");
        employees.createOrReplaceTempView("employees");
        titles.createOrReplaceTempView("titles");

        Dataset<Row> sqlDF = sparkSession.sql("SELECT first_name FROM salaries s Join employees e ON s.emp_no=e.emp_no WHERE salary>70000 GROUP BY first_name");
        System.out.println(sqlDF.queryExecution().optimizedPlan().numberedTreeString());
        /*Dataset<Row> sqlDF2 = sparkSession.sql("SELECT first_name FROM employees e JOIN (SELECT s.emp_no, salary FROM salaries s JOIN titles t " +
        "ON s.emp_no=t.emp_no GROUP BY s.salary HAVING t.title='Staff') st ON e.emp_no=st.emp_no GROUP BY first_name HAVING salary>70000");
        System.out.println(sqlDF2.queryExecution().optimizedPlan().numberedTreeString());*/

        // produce l'albero delle varie fasi di ottimizzazione
        System.out.println(sqlDF.queryExecution());

        String json = sqlDF.queryExecution().optimizedPlan().toJSON();
        System.out.println(json);

        ParserJson p = new ParserJson();
        System.out.println(p.parse("{\"data\": { \"translations\": [ { \"translatedText\": \"Hello world\"}]}}"));

        LogicalPlan plan = sqlDF.queryExecution().optimizedPlan();
        Collector c = new Collector();
        c.collect(plan);
        System.out.println(c.nodes.toString());
    }
}
