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

    public void collect(LogicalPlan plan) {

        operations = new HashMap<>();
        attributes = new HashMap<>();

        // crea una sequenza delle foglie del piano
        System.out.println(plan.collectLeaves());
        // stampa l'argomento come stringa dell'operazione
        System.out.println(plan.apply(4).argString());

        System.out.println(plan.apply(7).expressions().apply(0));
        System.out.println(plan.apply(4));

        int i = 0;
        while (plan.apply(i) != null) {
            operations.put(i, plan.apply(i).nodeName());
            List e = new ArrayList();
            //e.add(plan.apply(i).expressions().apply(0));
            i++;
        }
    }
}
