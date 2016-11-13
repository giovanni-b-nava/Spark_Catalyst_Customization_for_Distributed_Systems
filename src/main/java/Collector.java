import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Spark on 13/11/2016.
 */
public class Collector {

    public Map<String, List<String>> nodes;

    public void collect(LogicalPlan plan) {

        nodes = new HashMap<>();

        // crea una lista degli attributi coinvolti nell'operazione corrente
        System.out.println(plan.expressions());
        // ritorna il tipo di operazione corrente
        System.out.println(plan.nodeName());
        // ritorna il piano logico dei figli dell'operazione corrente
        System.out.println(plan.children());
        // crea una sequenza delle foglie del piano
        System.out.println(plan.collectLeaves());
        // crea una lista con la sequenza delle istruzioni json
        System.out.println(plan.jsonFields());

        System.out.println(plan.apply(2));

        /* Scrivo due appunti qui come promemoria: il ciclo qui sotto dovrà inserire in una mappa le operazioni con i
           relativi attributi coinvolti. Il problema è come recuperare le lunghezze delle liste per dare gli indici al while.
         */

        int i = 0;
        //plan.apply(i).toString() != "null"
        while (i<2) {
            List e = new ArrayList();
            e.add(plan.apply(i).expressions().apply(0));
            nodes.put(plan.apply(i).nodeName(), e);
            i++;
        }
    }
}
