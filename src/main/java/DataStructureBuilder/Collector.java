package DataStructureBuilder;

import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Spark on 13/11/2016.
 */
public class Collector {

    // Contains the ordered sequence of operations
    public Map<Integer, String> operations;
    // Contains the list of attributes involved in each operation
    public Map<Integer, List<String>> attributes;

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

    // Builds the two maps for operations and attributes
    public void collect(LogicalPlan plan) {

        operations = new HashMap<>();
        attributes = new HashMap<>();

        int i = 0;
        while (plan.apply(i) != null) {
            operations.put(i, plan.apply(i).nodeName());
            List e = this.collectAttributes(plan.apply(i));
            attributes.put(i, e);
            i++;
        }
    }
}
