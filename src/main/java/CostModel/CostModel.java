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

    private int getNumbersOfEncrypted()
    {
        return 0;
    }

}
