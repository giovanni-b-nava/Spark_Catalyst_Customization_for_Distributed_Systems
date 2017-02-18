package CostModel;

import RelationProfileTreeBuilder.Relation;
import RelationProfileTreeBuilder.RelationProfile;
import RelationProfileTreeBuilder.RelationProfileTree;
import TreeStructure.BinaryTree;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EncryptionProfile
{
    private Map<String, List<String>> map;

    public EncryptionProfile()
    {
        map = new HashedMap();
    }

    // Create a NEW copy of a EncryptionProfile
    public EncryptionProfile(EncryptionProfile profile)
    {
        this();

        this.map.putAll(profile.map);
    }

    // Assign the all the encryption types to all the attributes
    public void assignDefaultProfiles(BinaryTree<Relation> tree)
    {
        // TODO Modificare json e leggere da json i tipi di cifratura
        List<String> supportedEncryption = new ArrayList<>();

        supportedEncryption.add("aes");
        supportedEncryption.add("homomorphic");

        List<Relation> relations = tree.DFSVisit();

        // For every relation in the tree...
        for (int i=0; i<relations.size(); i++)
        {
            // If is the current operation is a LogicalRelation...
            if (relations.get(i).getOperation().equals("LogicalRelation"))
            {
                // For every attribute of the LogicalRelation...
                for (int j=0; j< relations.get(i).getAttributes().size(); j++)
                {
                    map.put(relations.get(i).getAttributes().get(j), supportedEncryption);
                }
            }
        }

    }

    // Update the EncryptionProfile of the current relation
    public void updateEncryptionProfile(Relation relation)
    {
        switch (relation.getOperation())
        {
            // <=, <, >=, >
            case "lessthanorequal":
            case "lessthan":
            case "greaterthanorequal":
            case "greaterthan":

                List<String> updated = new ArrayList<>();
                updated.add("homomorphic");

                for (int i=0; i<relation.getAttributes().size(); i++)
                {
                    if (map.containsKey(relation.getAttributes().get(i)))
                    {
                       // Replace the value with updated encryption
                       map.put(relation.getAttributes().get(i), updated);
                    }
                }
                break;
            default:
                System.out.println("updateEncryptionProfile.DEFAULT");
                break;
        }
    }

}
