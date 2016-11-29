import AuthorizationModel.AuthorizationModel;
import ConfigurationParser.Node;
import CostModel.CostModel;
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
        RelationProfileTree c = new RelationProfileTree(sqlDF.queryExecution().optimizedPlan());

        // Generate the tree for the authorized subjects
        AuthorizationModel m = new AuthorizationModel(DataBuilder.getDataBuilder().nodes, c.getRelationTree());

        // produce l'albero ottimizzato numerato
        System.out.println(sqlDF.queryExecution().optimizedPlan().numberedTreeString());
        // produce l'albero delle varie fasi di ottimizzazione
        //System.out.println(sqlDF.queryExecution());
        // produce la lista degli attributi coinvolti nell'operazione
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).references());

        // istruzioni per stampare gli operatori di ogni operazione
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).expressions().toList().apply(0));
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(2).constraints().toList());
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).prettyName());
        //System.out.println(sqlDF.queryExecution().optimizedPlan().apply(4).constraints().toList().apply(1).flatArguments().toList().apply(1));

        // Generazione strutture dati dell'albero
        List<Relation> relations = c.getRelationTree().DFSVisit();
        System.out.println(relations);

        List<List<Node>> n = m.getSubjectTree().DFSVisit();
        for (int i = 0; i < n.size(); i++) {
            System.out.println("Nodo " + i + ":");
            for (int x = 0; x < n.get(i).size(); x++) {
                System.out.println(n.get(i).get(x).getName());
            }
        }

        // COST TEST of JOIN
        BinaryNode<Relation> newNode = c.getRelationTree().getRoot().getLeft().getLeft();

        CostModel costModel = new CostModel();
//        double cost = costModel.computeCost(DataBuilder.getDataBuilder().nodes.get(0), DataBuilder.getDataBuilder().nodes.get(1), newNode);
//        System.out.println("---> Da " + DataBuilder.getDataBuilder().nodes.get(0).getName() + " a " + DataBuilder.getDataBuilder().nodes.get(1).getName());
//        System.out.println("---> OPERATION = " + newNode.getElement().getOperation());
//        System.out.println("---> COSTO = " + cost);
//
//        cost = costModel.computeCost(DataBuilder.getDataBuilder().nodes.get(1), DataBuilder.getDataBuilder().nodes.get(2), newNode);
//        System.out.println("---> Da " + DataBuilder.getDataBuilder().nodes.get(1).getName() + " a " + DataBuilder.getDataBuilder().nodes.get(2).getName());
//        System.out.println("---> OPERATION = " + newNode.getElement().getOperation());
//        System.out.println("---> COSTO = " + cost);
//
//        cost = costModel.computeCost(DataBuilder.getDataBuilder().nodes.get(2), DataBuilder.getDataBuilder().nodes.get(0), newNode);
//        System.out.println("---> Da " + DataBuilder.getDataBuilder().nodes.get(2).getName() + " a " + DataBuilder.getDataBuilder().nodes.get(0).getName());
//        System.out.println("---> OPERATION = " + newNode.getElement().getOperation());
//        System.out.println("---> COSTO = " + cost);


        // TEST generateSubplans
        costModel.generateSubplans(newNode, DataBuilder.getDataBuilder().nodes);

        // Generazione strutture dati dell'albero
        relations = c.getRelationTree().DFSVisit();
        System.out.println(relations);

    }
}
