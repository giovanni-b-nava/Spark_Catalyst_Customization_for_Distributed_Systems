package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Spark on 17/11/2016.
 */
public class Evaluator {

    // Generate the relation profiles for the leaves
    public List<RelationProfile> evaluateLeaves(Map<Integer, String> operations, Map<Integer, List<String>> attributes) {

        List<RelationProfile> leaves = new ArrayList<>();

        for(int i = 0; i < operations.size(); i++) {
            RelationProfile profile;
            if(operations.get(i).equals("LogicalRelation")) {
                profile = new RelationProfile(attributes.get(i), null, null, null, null);
                leaves.add(profile);
            }
        }
        return leaves;
    }

    // Generate the relation profiles for the operations
    public Map<Integer, RelationProfile> evaluateOperations(Map<Integer, String> operations, Map<Integer, List<String>> attributes, List<RelationProfile> leaves) {

        Map<Integer, RelationProfile> profileList = new HashMap<>();

        //TODO creare il profilo di ogni operatione a partire dal profilo dell'operazione che la precede
        for(int i = operations.size()-1, y = 0; i > 0; i--, y++) {

            List<RelationProfile> profiles = new ArrayList();
            //RelationProfile profile = new RelationProfile(profiles.get(y));

            //profiles.add(profile.generateProfile(operations.get(i), attributes.get(i)));
        }
        return profileList;
    }

    private RelationProfile generateProfile(RelationProfile profile, String operation, List<String> attributes) {

        //TODO modificare il profilo a seconda dell'operazione corrente
        RelationProfile p;

        switch (operation) {
            case "Aggregate":
                break;
            case "Project":
                break;
            case "Filter":
                break;
            case "Join":
                break;
            case "Logicalrelation":
                break;
            default:
                System.out.println("default");
        }
        return profile;
    }
}
