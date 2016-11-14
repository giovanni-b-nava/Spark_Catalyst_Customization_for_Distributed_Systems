import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Spark on 13/11/2016.
 */
public class Collector {

    public Map<Integer, String> operations;
    public Map<Integer, List<String>> attributes;

    private List<String> collectAttributes(LogicalPlan plan) {

        List<String> l = new ArrayList<>();

        switch(plan.nodeName()) {
            case "Project":
            case "Aggregate":
            case "Join":
            case "Filter":
                int i = 0;
                while(i < plan.expressions().toList().length()) {
                    String s = plan.expressions().apply(i).toString();
                    l.add(s);
                    i++;
                }
                break;
            case "LogicalRelation":
                int f = 0;
                while(f < plan.output().size()) {
                    String s = String.valueOf(plan.output().apply(f));
                    l.add(s);
                    f++;
                }
            default:
                System.out.println("default");
        }
        return l;
    }

    public void collect(LogicalPlan plan) {

        operations = new HashMap<>();
        attributes = new HashMap<>();

        int i = 0;
        while (plan.apply(i) != null) {
            operations.put(i, plan.apply(i).nodeName());
            List e = collectAttributes(plan.apply(i));
            attributes.put(i, e);
            i++;
        }
    }
}
