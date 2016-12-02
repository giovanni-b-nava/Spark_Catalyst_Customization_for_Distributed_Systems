import AuthorizationModel.AuthorizationModel;
import CostModel.CostModel;
import CostModel.PlansMap;
import CostModel.Plan;
import DataConfigBuilder.DataBuilder;
import RelationProfileTreeBuilder.Relation;
import RelationProfileTreeBuilder.RelationProfileTree;
import TreeStructure.BinaryNode;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Descrizione della classe
 *  */
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

        // Query
        Dataset<Row> sqlDF = DataBuilder.getDataBuilder().sparkSession.sql("SELECT first_name FROM salaries s Join employees e ON s.emp_no=e.emp_no WHERE salary>70000 GROUP BY first_name");

        // Generate the relation tree
        RelationProfileTree tree = new RelationProfileTree(sqlDF.queryExecution().optimizedPlan());

        // Generate the tree for the authorized subjects
        AuthorizationModel m = new AuthorizationModel(DataBuilder.getDataBuilder().providers, tree.getRelationTree());

        // produce l'albero ottimizzato numerato
        //System.out.println(sqlDF.queryExecution().optimizedPlan().numberedTreeString());

        // istruzioni per stampare gli operatori di ogni operazione
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).expressions().toList().apply(0));
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(2).constraints().toList());
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).prettyName());
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).flatArguments().toList().apply(1));

        // Generazione strutture dati dell'albero
        List<Relation> relations = tree.getRelationTree().DFSVisit();
        //System.out.println(relations);

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


        // JOIN TEST
//        BinaryNode<Relation> joinNode = tree.getRelationTree().getRoot().getLeft().getLeft();
//        BinaryNode<Relation> leftProjectionNode = tree.getRelationTree().getRoot().getLeft().getLeft().getLeft();
//        BinaryNode<Relation> rightProjectionNode = tree.getRelationTree().getRoot().getLeft().getLeft().getRight();

        //RelationProfile newRelationProfile = costModel.updateRelationProfile(DataBuilder.getDataBuilder().providers.get(2), joinNode);

        //System.out.println("newRelationProfile.toString():\n" + newRelationProfile.toString());

        PlansMap plansMap = costModel.generatePlans(tree.getRelationTree().getRoot());
        Plan optimalPlan = costModel.getOptimalPlan(plansMap);
        System.out.println("-----> OPTIMAL PLAN:");
        System.out.println(optimalPlan.toString());

    }

}
