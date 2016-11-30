package CostModel;

import AuthorizationModel.AuthorizationModel;
import ConfigurationParser.Node;
import RelationProfileTreeBuilder.Relation;
import RelationProfileTreeBuilder.RelationProfile;
import TreeStructure.BinaryNode;

import java.util.List;

/**
 * Created by Giovanni on 28/11/2016.
 */
public class CostModel
{


    public void generateSubplans(BinaryNode<Relation> relationNode, List<Node> nodes)
    {
        Node providerTo;
        Node providerFrom;

        // TODO fare controllo per gestire il caso della LogicalRelation

        // For all the from providers...
        for (int i=0; i < nodes.size(); i++)
        {
            providerFrom = nodes.get(i);
            // For all the to providers...
            for (int j=0; j < nodes.size(); j++)
            {
                providerTo = nodes.get(j);

                // Update the relation profile of the father
                RelationProfile updatedProfile = updateRelationProfile(providerTo, relationNode);
                relationNode.getFather().getElement().setRelationProfile(updatedProfile);
                // Compute the relation cost
                double relationCost = computeCost(providerFrom, providerTo, relationNode);
                // Add the subplan(hashcode, cost) to the relation
                relationNode.getElement().getSubplansMap().addSubplan(providerFrom, relationNode.getElement(), relationCost);

            }
        }
    }

    // Generate the updated profile updating (if needed) Encryption or Decryption BEFORE computing the cost
    private RelationProfile updateRelationProfile(Node providerTo, BinaryNode<Relation> relationNode)
    {
        RelationProfile fatherProfile = relationNode.getFather().getElement().getRelationProfile();
        RelationProfile currentProfile = relationNode.getElement().getRelationProfile();

        RelationProfile updatedProfile = new RelationProfile(fatherProfile);

        // For all the father's visible plaintext attributes ...
        for (int i=0; i < fatherProfile.getVisiblePlaintext().size(); i++)
        {
            String currentAttribute = fatherProfile.getVisiblePlaintext().get(i);

            // If the current attribute visibility is Plaintext for provider to ...
            if (AuthorizationModel.checkVisibility(providerTo, currentAttribute,"Plaintext"))
            {
                // If current profile doesn't contain in the visible plaintext the current attribute...
                if (!currentProfile.getVisiblePlaintext().contains(currentAttribute))
                {
                    // If current profile contains in the visibile encrypted the current attribute
                    if (currentProfile.getVisibleEncrypted().contains(currentAttribute))
                    {
                        // Update the relation profile moving the attribute from the visible plaintext
                        // to the visible encrypted
                        updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                        updatedProfile.getVisibleEncrypted().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.UpdateRelationProfile: ERROR the attribute is not visible (visibility#1)");
                }
            }
            // The current attribute visibility is Encrypted for providerTo
            else
            {
                // If current profile doesn't contain in the visible encrypted the current attribute...
                if (!currentProfile.getVisibleEncrypted().contains(currentAttribute))
                {
                    // If current profile contains in the visible plaintext the current attribute
                    if (currentProfile.getVisiblePlaintext().contains(currentAttribute)) {
                        // Update the relation profile moving the attribute from the visible plaintext
                        // to the visible encrypted
                        updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                        updatedProfile.getVisibleEncrypted().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.UpdateRelationProfile: ERROR the attribute is not visible (visibility#2)");
                }
            }
        }

        // For all the father's visible encrypted attributes ...
        for (int i=0; i < fatherProfile.getVisibleEncrypted().size(); i++)
        {
            String currentAttribute = fatherProfile.getVisibleEncrypted().get(i);

            // If the current attribute visibility is Encrypted for provider to ...
            if (AuthorizationModel.checkVisibility(providerTo, currentAttribute,"Encrypted"))
            {
                // If current profile doesn't contain in the visible encrypted the current attribute...
                if (!currentProfile.getVisibleEncrypted().contains(currentAttribute))
                {
                    // If current profile contains in the visible plaintext the current attribute
                    if (currentProfile.getVisiblePlaintext().contains(currentAttribute))
                    {
                        // Update the relation profile moving the attribute from the visible encrypted
                        // to the visible plaintext
                        updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                        updatedProfile.getVisiblePlaintext().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.UpdateRelationProfile: ERROR invalid attribute (visibility#3)");
                }
            }
            // The current attribute visibility is Plaintext for provider to
            else
            {
                // If current profile doesn't contain in the visible encrypted the current attribute...
                if (!currentProfile.getVisibleEncrypted().contains(currentAttribute))
                {
                    // If current profile contains in the visible encrypted the current attribute
                    if (currentProfile.getVisibleEncrypted().contains(currentAttribute))
                    {
                        // Update the relation profile moving the attribute from the visible encrypted
                        // to the visible plaintext
                        updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                        updatedProfile.getVisiblePlaintext().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.UpdateRelationProfile: ERROR invalid attribute (visibility#4)");
                }
            }
        }

        return updatedProfile;
    }

    // ************************************************************************
    // Cost Computation

    // TODO private
    public double computeCost(Node providerFrom, Node providerTo, BinaryNode<Relation> relationNode)
    {
        // Dimensions in Giga Bytes
        double totalGB = relationNode.getElement().getSyzeInBytes() * Math.pow(10, -9);

        // Represents the single operation cost
        // [ $ ]
        double operationCost = getOperationCost(providerFrom, totalGB, relationNode.getElement().getOperation());

        // Represents the proportion (encrypted attributes / total attributes)
        double encryptionPercent = getNumbersOfEncrypted(providerTo, relationNode) / (relationNode.getFather().getElement().getRelationProfile().getVisiblePlaintext().size() + relationNode.getFather().getElement().getRelationProfile().getVisibleEncrypted().size());

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
