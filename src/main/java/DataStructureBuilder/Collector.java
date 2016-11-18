package DataStructureBuilder;


import TreeStructure.BinaryNode;
import TreeStructure.BinaryTree;
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Spark on 13/11/2016.
 */
public class Collector
{

    // Tree representing the relations in the optimized plan
    BinaryTree<Relation> tree;

    // Builds the two maps for operations and attributes
    public void buildTree(LogicalPlan plan) {

        // Generate the root of the tree
        //Relation relation = this.createRelation(plan.apply(0));
        //tree = new BinaryTree<>(relation);

        // Generate the rest of the tree
        BinaryNode<Relation> current = new BinaryNode<>(null);
        tree = new BinaryTree<>(current);
        int i = 0;
        while (plan.apply(i) != null)
        {
            current = this.generateNode(plan.apply(i), current);
            i++;
        }
        System.out.println(tree.getRoot().getElement().getOperator());
    }

    private BinaryNode<Relation> generateNode(LogicalPlan plan, BinaryNode<Relation> father) {

        Relation e = this.createRelation(plan);
        BinaryNode<Relation> node = new BinaryNode<>(e);
        node.setFather(father);

        if(plan.children().size() == 1) {
            Relation r = this.createRelation(plan.children().toList().apply(0));
            BinaryNode n = new BinaryNode(r);
            node.setLeft(n);
        }
        else if(plan.children().size() == 2) {
            Relation r1 = this.createRelation(plan.children().toList().apply(0));
            BinaryNode n1 = new BinaryNode(r1);
            node.setLeft(n1);
            Relation r2 = this.createRelation(plan.children().toList().apply(1));
            BinaryNode n2 = new BinaryNode(r2);
            node.setRight(n2);
        }
        return node;
    }

    // Generate the relation of the current level
    private Relation createRelation(LogicalPlan plan) {
        String operation = plan.nodeName();
        List e = this.collectAttributes(plan);
        return new Relation(operation, e);
    }

    // Generates the list of attributes for the current operation
    private List<String> collectAttributes(LogicalPlan plan) {

        List<String> l = new ArrayList<>();

        switch(plan.nodeName()) {
            //TODO migliorare la generazione di join e filter (non vedono gli operatori coinvolti)
            case "Project":
            case "Aggregate":
                int i = 0;
                while(i < plan.expressions().toList().length()) {
                    String s = plan.expressions().apply(i).toString();
                    l.add(s);
                    i++;
                }
                break;
            case "Join":
            case "Filter":
                int x=0;
                while(x < plan.apply(0).constraints().toList().size()) {
                    String s;
                    if(plan.apply(0).constraints().toList().apply(x).flatArguments().toList().size() == 1) {
                        s = plan.apply(0).constraints().toList().apply(x).flatArguments().toList().apply(0).toString();
                        l.add(s);
                    } else {
                        s = plan.apply(0).constraints().toList().apply(x).prettyName();
                        l.add(s);
                    }
                    x++;
                }
                break;
            case "LogicalRelation":
                int f = 0;
                while(f < plan.output().size()) {
                    String s = String.valueOf(plan.output().apply(f));
                    l.add(s);
                    f++;
                }
                break;
            default:
                System.out.println("default");
        }
        return l;
    }
}
