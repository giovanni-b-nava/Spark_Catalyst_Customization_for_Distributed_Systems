package CostModel;

import ConfigurationParser.Node;
import RelationProfileTreeBuilder.Relation;
import TreeStructure.BinaryNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giovanni on 24/11/2016.
 */
public class CostModel
{


    public double computeCost(Node providerFrom, Node providerTo, BinaryNode<Relation> relationNode)
    {
        double encryptedBytes = 0;
        double plaintextBytes = 0;
        double encryptionCost = 0;
        double transferCost   = 0;
        double operationCost  = 0;

        // Represents the proportion (encrypted attributes / total attributes)
        double encryptionPercentage = getNumbersOfEncrypted(providerFrom, providerTo, relationNode) / (relationNode.getElement().getProfile().getVisiblePlaintext().size() + relationNode.getElement().getProfile().getVisibleEncrypted().size());

        // Represents the encryption cost ( (bytes encrypted) / (encryption cost + cpu cost) )
        // [ $/hour ]
        encryptionCost = (relationNode.getElement().getSyzeInBytes() * encryptionPercentage) / (providerFrom.getCosts().getEncryption() * providerFrom.getCosts().getCpu());

        // Represent the transfer cost from children to father
        transferCost = relationNode.getElement().getSyzeInBytes() * (relationNode.getElement().getSyzeInBytes() / ((findThroughput(providerFrom, providerTo) * (providerFrom.getCosts().getCpu() + providerTo.getCosts().getCpu()))));

        operationCost = getOperationCost(relationNode.getElement().getSyzeInBytes(), relationNode.getElement().getOperation());

        return (encryptionCost + transferCost + operationCost) / (10e6);
    }

    private double getNumbersOfEncrypted(Node providerFrom, Node providerTo, BinaryNode<Relation> relationNode)
    {
        int counter = 0;
        // For all the attributes in the Father ...
        for (int j=0; j < relationNode.getFather().getElement().getAttributes().size(); j++)
        {
            // ... for all the tables of the destination ...
            for (int i=0; i < providerTo.getTables().size(); i++)
            {
                // ... if an encrypted destination table contains the attribute
                if (providerTo.getTables().get(i).getEncrypted().contains(relationNode.getFather().getElement().getAttributes().get(j)))
                {
                    // count che number of the attributes to be sent encrypted
                    counter++;
                    break;
                }
            }
        }

        return counter;
    }

    private double findThroughput(Node providerFrom, Node providerTo)
    {
        List<String> linksName = providerFrom.getLinks().getName();
        int index = linksName.indexOf(providerTo.getName());

        // return the throughput
        return providerFrom.getLinks().getThroughput().get(index);
    }

    private double getOperationCost(long operationSize, String operationType)
    {
        double operationCost = 0;

        switch (operationType)
        {
            case "Filter" :
            case "Project" :
                // 10 MBps
                operationCost = operationSize / (10 * 10e-6);
                break;
            case "Aggregate" :
                // 7 MBps
                operationCost = operationSize / (7 * 10e-6);
                break;
            case "Join" :
                // 1 MBps
                operationCost = operationSize / (1 * 10e-6);
                break;
            default:
                System.out.println("CostModel.getOperationCost: ERROR Unknown operation !");
        }

        return operationCost;
    }

}
