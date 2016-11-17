package Model;

import ParserConfigurator.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Spark on 17/11/2016.
 */
public class AuthorizationModel {

    // Generate the list of subjects authorized to execute each operation
    public Map<Integer, List<String>> authorizedSubjects(Map<Integer, String> operations, Map<Integer, List<String>> attributes, List<Node> nodes) {

        Map<Integer, List<String>> subjectsLists = new HashMap<>();
        Evaluator evaluator = new Evaluator();

        // Generate the profiles of the leaves
        List<RelationProfile> leaves = evaluator.evaluateLeaves(operations, attributes);

        // Generate the relation profiles for the operations
        Map<Integer, RelationProfile> profiles = evaluator.evaluateOperations(operations, attributes, leaves);

        for(int i = operations.size()-1; i > 0; i--) {

            List<String> subjects = new ArrayList<>();

            // The leaves can't have subjects
            if(!operations.get(i).equals("LogicalRelation")) {
                for (int x = 0; x < nodes.size(); x++) {
                    // Add the authorized nodes to the list
                    if (this.authorized(profiles.get(i), nodes.get(x))) {
                        subjects.add(nodes.get(x).getName());
                    }
                }
            }
            subjectsLists.put(i, subjects);
        }
        return subjectsLists;
    }

    // Return true if the node is authorized to execute the operation
    private boolean authorized(RelationProfile profile, Node node) {
        //TODO implementare le tre regole per verificare se un soggetto è autorizzato


        return true;
    }
}
