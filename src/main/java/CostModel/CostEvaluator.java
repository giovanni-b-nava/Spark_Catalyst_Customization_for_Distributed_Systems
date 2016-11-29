package CostModel;

import ConfigurationParser.Node;
import RelationProfileTreeBuilder.Relation;
import TreeStructure.BinaryNode;

import java.util.List;

/**
 * Created by Giovanni on 24/11/2016.
 */
public class CostEvaluator
{


    public double computeCost(Node providerFrom, Node providerTo, BinaryNode<Relation> relationNode)
    {
        // Dimensions in Giga Bytes
        double totalGB = relationNode.getElement().getSyzeInBytes() * Math.pow(10, -9);

        // Represents the single operation cost
        // [ $ ]
        double operationCost = getOperationCost(providerFrom, totalGB, relationNode.getElement().getOperation());

        // Represents the proportion (encrypted attributes / total attributes)
        double encryptionPercent = getNumbersOfEncrypted(providerTo, relationNode) / (relationNode.getFather().getElement().getProfile().getVisiblePlaintext().size() + relationNode.getFather().getElement().getProfile().getVisibleEncrypted().size());

        // Represents the encryption cost ( ( bytes encrypted / (cpu speed * encryption overhead)) *  cpu cost)
        // [ $ ]
        double encryptionCost = ((totalGB * encryptionPercent) / (providerFrom.getCosts().getCpuSpeed() * providerFrom.getCosts().getEncryption())) * providerFrom.getCosts().getCpu();

        // Represent the transfer cost from children to father
        double transferCost = totalGB * findCostPerGB(providerFrom, providerTo);

        return (encryptionCost + transferCost + operationCost);
    }

    private double getNumbersOfEncrypted(Node providerTo, BinaryNode<Relation> relationNode)
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

    private double findCostPerGB(Node providerFrom, Node providerTo)
    {
        List<String> linksName = providerFrom.getLinks().getName();
        int index = linksName.indexOf(providerTo.getName());

        // return the right cost per GB
        return providerFrom.getLinks().getCostPerGB().get(index);
    }

    private double getOperationCost(Node providerFrom, double totalGB, String operationType)
    {
        double operationCost = 0;

        switch (operationType)
        {
            case "Filter" :
            case "Project" :
                operationCost = 1;
                break;
            case "Aggregate" :
                operationCost = 0.7;
                break;
            case "Join" :
                operationCost = 0.1;
                break;
            default:
                System.out.println("CostEvaluator.getOperationCost: ERROR Unknown operation !");
        }

        return ((totalGB / (providerFrom.getCosts().getCpuSpeed() * operationCost)) * providerFrom.getCosts().getCpu());
    }

}
