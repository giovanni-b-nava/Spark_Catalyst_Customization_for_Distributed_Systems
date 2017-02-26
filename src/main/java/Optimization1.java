/*
    ----------------------------------------------------------------------------------------------------
    "Spark Catalyst Customization for Distributed Systems"
    ----------------------------------------------------------------------------------------------------
    European Research Project “Enforceable Security in the Cloud to Uphold Data Ownership” (ESCUDO-CLOUD)
    ----------------------------------------------------------------------------------------------------
    Release 1.0 by Antonio Cosseddu and Giovanni B. Nava
    Release 2.0 by Giovanni B. Nava
    ----------------------------------------------------------------------------------------------------
 */

import AuthorizationModel.*;
import CostModel.*;
import DataConfigBuilder.DataBuilder;
import RelationProfileTreeBuilder.*;
import TreeStructure.BinaryNode;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import java.io.FileNotFoundException;


public class Optimization1
{

    public static void main(String[] args) throws FileNotFoundException
    {

        DataBuilder.getDataBuilder().buildData();

        // Create views for the query
        DataBuilder.getDataBuilder().salaries.createOrReplaceTempView("salaries");
        DataBuilder.getDataBuilder().employees.createOrReplaceTempView("employees");
        DataBuilder.getDataBuilder().titles.createOrReplaceTempView("titles");
        DataBuilder.getDataBuilder().departments.createOrReplaceTempView("departments");
        DataBuilder.getDataBuilder().dept_emp.createOrReplaceTempView("dept_emp");
        DataBuilder.getDataBuilder().dept_manager.createOrReplaceTempView("dept_manager");

        // QUERY
        Dataset<Row> sqlDF = DataBuilder.getDataBuilder().sparkSession.sql("SELECT first_name FROM salaries s Join employees e ON s.emp_no=e.emp_no WHERE salary > 70000 GROUP BY first_name");

        // Generate the relation tree
        RelationProfileTree tree = new RelationProfileTree(sqlDF.queryExecution().optimizedPlan());

        // Generate the tree for the authorized subjects
        AuthorizationModel model = new AuthorizationModel(DataBuilder.getDataBuilder().providers, tree.getRelationTree());

        // produce l'albero ottimizzato numerato
        //System.out.println(sqlDF.queryExecution().optimizedPlan().numberedTreeString());

        // istruzioni per stampare gli operatori di ogni operazione
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).expressions().toList().apply(0));
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(2).constraints().toList());
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).prettyName());
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).flatArguments().toList().apply(1));

        // COST TEST of JOIN
        BinaryNode<Relation> newNode = tree.getRelationTree().getRoot().getLeft().getLeft().getLeft().getLeft();

        CostModel costModel = new CostModel(DataBuilder.getDataBuilder().providers, tree);
//        double cost = costModel.computeCost(DataBuilder.getDataBuilder().providers.get(0), DataBuilder.getDataBuilder().providers.get(1), newNode);
//        System.out.println("---> Da " + DataBuilder.getDataBuilder().providers.get(0).getName() + " a " + DataBuilder.getDataBuilder().providers.get(1).getName());
//        System.out.println("---> OPERATION = " + newNode.getElement().getOperation());
//        System.out.println("---> COSTO = " + cost);
//
//        cost = costModel.computeCost(DataBuilder.getDataBuilder().providers.get(1), DataBuilder.getDataBuilder().providers.get(2), newNode);
//        System.out.println("---> Da " + DataBuilder.getDataBuilder().providers.get(1).getName() + " a " + DataBuilder.getDataBuilder().providers.get(2).getName());
//        System.out.println("---> OPERATION = " + newNode.getElement().getOperation());
//        System.out.println("---> COSTO = " + cost);
//
//        cost = costModel.computeCost(DataBuilder.getDataBuilder().providers.get(2), DataBuilder.getDataBuilder().providers.get(0), newNode);
//        System.out.println("---> Da " + DataBuilder.getDataBuilder().providers.get(2).getName() + " a " + DataBuilder.getDataBuilder().providers.get(0).getName());
//        System.out.println("---> OPERATION = " + newNode.getElement().getOperation());
//        System.out.println("---> COSTO = " + cost);


        // Create a new EncryptionProfile
        EncryptionProfile encProfile = new EncryptionProfile();
        // Init the EncryptionProfile with all default encryptions
        encProfile.setupDefault(tree.getRelationTree());

        PlansMap plansMap = costModel.generatePlans(tree.getRelationTree().getRoot(), encProfile);

        Plan optimalPlan = costModel.getOptimalPlan(plansMap);

        System.out.println("*****************************************************");
        System.out.println("> [INFO] OPTIMAL PLAN:");
        System.out.println(optimalPlan.toString());
        System.out.println("*****************************************************");

    }

}