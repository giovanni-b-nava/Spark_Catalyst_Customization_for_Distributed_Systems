package CostModel;

import ConfigurationParser.Node;
import RelationProfileTreeBuilder.Relation;
import TreeStructure.BinaryNode;

/**
 * Created by Giovanni on 24/11/2016.
 */
public class CostModel
{
    public int computeCost(Node start, Node destination, BinaryNode<Relation> relationNode)
    {
        int encryptedBytes;
        int plaintextBytes;
        int encryptionCost = 0;
        int transferCost = 0;
        int operationCost = 0;

      //  int encryptionPercentage = (relation.getProfile().getImplicitEncrypted().size() + relation.getProfile().getVisibleEncrypted().size());

        // TODO

        return encryptionCost + transferCost + operationCost;
    }

    private int getNumbersOfEncrypted(Node start, Node destination, BinaryNode<Relation> relationNode)
    {
        int counter = 0;
        // For all the attributes in the Father ...
        for (int j=0; j < relationNode.getFather().getElement().getAttributes().size(); j++)
        {
            // ... for all the tables of the destination ...
            for (int i=0; i < destination.getTables().size(); i++)
            {
                // ... if an encrypted destination table contains the attribute
                if (destination.getTables().get(i).getEncrypted().contains(relationNode.getFather().getElement().getAttributes().get(j)))
                {
                    counter++;
                    break;
                }
            }
        }

        return counter;
    }

}
