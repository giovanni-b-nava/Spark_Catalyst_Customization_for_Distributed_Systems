package AuthorizationModel;

import RelationProfileTreeBuilder.RelationProfile;

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

    // Generate the specific profile for each operation
    private RelationProfile generateProfile(RelationProfile profile, String operation, List<String> attributes) {

        //TODO modificare il profilo (per il join servono due relazioni e per filter bisogna scandire le operazioni)
        RelationProfile p = new RelationProfile(null, null, null, null, null);

        switch (operation) {
            case "Aggregate":
                p.setVisiblePlaintext(attributes);
                p.setVisibleEncrypted(profile.getVisibleEncrypted());
                for(int i = 0; i < profile.getVisiblePlaintext().size(); i++) {
                    if(p.getVisiblePlaintext().contains(profile.getVisiblePlaintext().get(i))) {
                        p.getImplicitPlaintext().add(profile.getVisiblePlaintext().get(i));
                    }
                }
                p.setImplicitEncrypted(profile.getImplicitEncrypted());
                p.setEquivalenceSets(profile.getEquivalenceSets());
                break;
            case "Project":
                p.setVisiblePlaintext(attributes);
                p.setVisibleEncrypted(profile.getVisibleEncrypted());
                p.setImplicitPlaintext(profile.getImplicitPlaintext());
                p.setImplicitEncrypted(profile.getImplicitEncrypted());
                p.setEquivalenceSets(profile.getEquivalenceSets());
                break;
            case "Filter":
                break;
            case "Join":

                break;
            default:
                System.out.println("LogicalRelation or unknown");
        }
        return p;
    }

    // Support method to join two lists without duplicates
    private List<String> joinLists(List<String> l1, List<String> l2) {

        List<String> list = new ArrayList<>();

        list.addAll(l1);
        for(int i = 0; i < l2.size(); i++) {
            if(!list.contains(l2.get(i))) {
                list.add(l2.get(i));
            }
        }
        return list;
    }
}
