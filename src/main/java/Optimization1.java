import RelationProfileTreeBuilder.RelationProfileTree;
import DataConfigBuilder.DataBuilder;
import RelationProfileTreeBuilder.Relation;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.FileNotFoundException;
import java.util.List;


/**
 * Created by Spark on 11/11/2016.
 */
public class Optimization1 {

    public static void main(String[] args) throws FileNotFoundException {

        DataBuilder dataBuilder = new DataBuilder();
        dataBuilder.buildData();

        // Create views for the query
        dataBuilder.salaries.createOrReplaceTempView("salaries");
        dataBuilder.employees.createOrReplaceTempView("employees");
        dataBuilder.titles.createOrReplaceTempView("titles");

        // Query
        Dataset<Row> sqlDF = dataBuilder.sparkSession.sql("SELECT first_name FROM salaries s Join employees e ON s.emp_no=e.emp_no WHERE salary>70000 GROUP BY first_name");

        // Generate the relation tree
        RelationProfileTree c = new RelationProfileTree();
        c.buildTree(sqlDF.queryExecution().optimizedPlan());


        // produce l'albero ottimizzato numerato
        System.out.println(sqlDF.queryExecution().optimizedPlan().numberedTreeString());
        // produce l'albero delle varie fasi di ottimizzazione
        //System.out.println(sqlDF.queryExecution());
        // produce la lista degli attributi coinvolti nell'operazione
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).references());

        // istruzioni per stampare gli operatori di ogni operazione
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(2).children().toList().apply(0));
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList());
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).prettyName());
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).flatArguments().toList().apply(1));

        // Generazione strutture dati dell'albero
        List<Relation> l = c.relationTree.DFSVisit();
        System.out.println(l);
     }
}
