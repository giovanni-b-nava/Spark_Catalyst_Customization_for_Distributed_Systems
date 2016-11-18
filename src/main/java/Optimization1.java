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
        //System.out.println(sqlDF.queryExecution());
        // produce la lista degli attributi coinvolti nell'operazione
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).references());

        // istruzioni per stampare gli operatori di ogni operazione
        System.out.println(sqlDF.queryExecution().optimizedPlan().apply(2).children().size());
        System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList());
        System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).prettyName());
        System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).flatArguments().toList().apply(1));

        // Generazione strutture dati dell'albero
        Collector c = new Collector();
        c.buildTree(sqlDF.queryExecution().optimizedPlan());
        System.out.println("Operations list: ");
        //System.out.println(c.operations.toString());
        System.out.println("Attributes list: ");
        //System.out.println(c.attributes.toString());
     }
}
