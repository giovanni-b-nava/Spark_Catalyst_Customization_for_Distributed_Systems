import DataStructureBuilder.Collector;
import DataStructureBuilder.Generator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.FileNotFoundException;


/**
 * Created by Spark on 11/11/2016.
 */
public class Optimization1 {

    public static void main(String[] args) throws FileNotFoundException {

        Generator builder = new Generator();
        builder.buildData();

        // Create views for the query
        builder.salaries.createOrReplaceTempView("salaries");
        builder.employees.createOrReplaceTempView("employees");
        builder.titles.createOrReplaceTempView("titles");

        // Query
        Dataset<Row> sqlDF = builder.sparkSession.sql("SELECT first_name FROM salaries s Join employees e ON s.emp_no=e.emp_no WHERE salary>70000 GROUP BY first_name");

        // produce l'albero ottimizzato numerato
        System.out.println(sqlDF.queryExecution().optimizedPlan().numberedTreeString());
        // produce l'albero delle varie fasi di ottimizzazione
        System.out.println(sqlDF.queryExecution());

        System.out.println(sqlDF.queryExecution().optimizedPlan().apply(2).nodeName());
        System.out.println(sqlDF.queryExecution().optimizedPlan().apply(2).constraints().toList());
        System.out.println(sqlDF.queryExecution().optimizedPlan().apply(2).constraints().toList().apply(0).flatArguments().toList().apply(0));

        // Generazione strutture dati dell'albero
        Collector c = new Collector();
        c.collect(sqlDF.queryExecution().optimizedPlan());
        System.out.println("Operations: ");
        System.out.println(c.operations.toString());
        System.out.println("Attributes: ");
        System.out.println(c.attributes.toString());
     }
}
