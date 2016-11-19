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
    public BinaryTree<Relation> tree;

    // Builds the tree with the relations representing the operations
    public void buildTree(LogicalPlan plan) {

        // Generate the root of the tree
        Relation e = this.createRelation(plan);
        BinaryNode<Relation> root = new BinaryNode<>(e);
        tree = new BinaryTree<>(root);

        // Generate the rest of the tree
        this.generateNode(plan.apply(0), root);
    }

    // Recursively generate all the nodes in the tree
    private void generateNode(LogicalPlan plan, BinaryNode<Relation> father) {

        if(plan.children().size() == 1) {
            Relation r = this.createRelation(plan.children().toList().apply(0));
            BinaryNode n = new BinaryNode(r);
            father.setLeft(n);
            n.setFather(father);
            this.generateNode(plan.children().toList().apply(0), n);
        }
        else if(plan.children().size() == 2) {
            Relation r1 = this.createRelation(plan.children().toList().apply(0));
            BinaryNode n1 = new BinaryNode(r1);
            father.setLeft(n1);
            n1.setFather(father);
            this.generateNode(plan.children().toList().apply(0), n1);
            Relation r2 = this.createRelation(plan.children().toList().apply(1));
            BinaryNode n2 = new BinaryNode(r2);
            father.setRight(n2);
            n2.setFather(father);
            this.generateNode(plan.children().toList().apply(1), n2);
        }
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
