package DataStructureBuilder;


import TreeStructure.NTL;
import TreeStructure.TNodeList;
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Spark on 13/11/2016.
 */
public class Collector {

    // Tree representing the relations in the optimized plan
    NTL relations;

    // Builds the two maps for operations and attributes
    public void collect(LogicalPlan plan) {

        //TODO operazioni e attributi vanno rappresentati con un albero

        // Generate the root of the tree
        Relation r = this.createRelation(plan.apply(0));
        TNodeList root = new TNodeList(r);
        relations = new NTL(root);

        // Generate the rest of the tree
        int i = 1;
        while (plan.apply(i) != null) {
            for(int x = 0; x < plan.apply(i).children().size(); x++) {
                Relation r = this.createRelation(plan.apply(i));
                DefaultMutableTreeNode n = new DefaultMutableTreeNode(r);
            }
            i++;
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
