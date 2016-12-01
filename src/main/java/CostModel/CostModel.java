package CostModel;

import AuthorizationModel.AuthorizationModel;
import ConfigurationParser.Provider;
import RelationProfileTreeBuilder.Relation;
import RelationProfileTreeBuilder.RelationProfile;
import TreeStructure.BinaryNode;

import java.util.List;

/**
 * Created by Giovanni on 28/11/2016.
 */
public class CostModel
{

    // TODO check utility... :(
    public void generateOptimalPlan(BinaryNode<Relation> root, List<Provider> providers)
    {
        Provider providerTo;
        Provider providerFrom;

        // TODO fare controllo per gestire il caso della LogicalRelation

        // For all the from providers...
        for (int i = 0; i < providers.size(); i++)
        {
            providerFrom = providers.get(i);
            // For all the to providers...
            for (int j = 0; j < providers.size(); j++)
            {
                providerTo = providers.get(j);

                // Update the relation profile of the father
                RelationProfile updatedProfile = updateRelationProfile(providerTo, root);
                root.getFather().getElement().setRelationProfile(updatedProfile);
                // Compute the relation cost
                double relationCost = computeCost(providerFrom, providerTo, root);


            }
        }
    }

    // Generate the updated profile updating (if needed) Encryption or Decryption BEFORE computing the cost
    private RelationProfile updateRelationProfile(Provider currentProvider, BinaryNode<Relation> relationNode)
    {
        RelationProfile currentProfile = relationNode.getElement().getRelationProfile();
        RelationProfile leftChildProfile = relationNode.getLeft().getElement().getRelationProfile();
        RelationProfile rightChildProfile = relationNode.getRight().getElement().getRelationProfile();

        RelationProfile updatedProfile = new RelationProfile(currentProfile);

        // Filter, Project, Aggregate
        if (leftChildProfile != null && rightChildProfile == null)
        {
            update1Child(currentProfile, leftChildProfile, updatedProfile, currentProvider);
        }
        // Join
        else if (leftChildProfile != null && rightChildProfile != null)
        {
            update2Children(currentProfile, leftChildProfile, rightChildProfile, updatedProfile, currentProvider);

            // Check if the attributes of the Join have the same visibility
            String firstAttribute = relationNode.getElement().getAttributes().get(0);
            String secondAttribute = relationNode.getElement().getAttributes().get(1);
            // Assumption: if one of the two attributes is in plaintext the decrypt the other
            if(updatedProfile.getVisiblePlaintext().contains(firstAttribute) && !updatedProfile.getVisiblePlaintext().contains(secondAttribute)) {
                // Update the relation profile moving the second attribute from the visible encrypted
                // to the visible plaintext
                updatedProfile.getVisiblePlaintext().remove(secondAttribute);
                updatedProfile.getVisibleEncrypted().add(secondAttribute);
            }
            else if(!updatedProfile.getVisiblePlaintext().contains(firstAttribute) && updatedProfile.getVisiblePlaintext().contains(secondAttribute)) {
                // Update the relation profile moving the first attribute from the visible encrypted
                // to the visible plaintext
                updatedProfile.getVisiblePlaintext().remove(firstAttribute);
                updatedProfile.getVisibleEncrypted().add(firstAttribute);
            }
        }
        // else: for a Logical Relation don't do anything

        return updatedProfile;
    }

    // Update the profile with encryption and decryption (if needed) for the operations Filter, Project, Aggregate
    private void update1Child(RelationProfile currentProfile, RelationProfile leftChildProfile, RelationProfile updatedProfile, Provider currentProvider)
    {
        // For all the currentProfile's visible plaintext attributes ...
        for (int i=0; i < currentProfile.getVisiblePlaintext().size(); i++)
        {
            String currentAttribute = currentProfile.getVisiblePlaintext().get(i);

            // If the current attribute visibility is Plaintext for the current provider...
            if (AuthorizationModel.checkVisibility(currentProvider, currentAttribute,"Plaintext"))
            {
                // If the child profile doesn't contain in the visible plaintext the current attribute...
                if (!leftChildProfile.getVisiblePlaintext().contains(currentAttribute))
                {
                    // If the child profile contains in the visible encrypted the current attribute
                    if (leftChildProfile.getVisibleEncrypted().contains(currentAttribute))
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
            // The current attribute visibility is Encrypted for currentProvider
            else
            {
                // If the child profile doesn't contain in the visible encrypted the current attribute...
                if (!leftChildProfile.getVisibleEncrypted().contains(currentAttribute))
                {
                    // If the child profile contains in the visible plaintext the current attribute
                    if (leftChildProfile.getVisiblePlaintext().contains(currentAttribute)) {
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

        // For all the currentProfile's visible encrypted attributes ...
        for (int i=0; i < currentProfile.getVisibleEncrypted().size(); i++)
        {
            String currentAttribute = currentProfile.getVisibleEncrypted().get(i);

            // If the current attribute visibility is Encrypted for the current provider ...
            if (AuthorizationModel.checkVisibility(currentProvider, currentAttribute, "Encrypted"))
            {
                // If the child profile doesn't contain in the visible encrypted the current attribute...
                if (!leftChildProfile.getVisibleEncrypted().contains(currentAttribute))
                {
                    // If the child profile contains in the visible plaintext the current attribute
                    if (leftChildProfile.getVisiblePlaintext().contains(currentAttribute))
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
            // The current attribute visibility is Plaintext for the current provider
            else
            {
                // If the child profile doesn't contain in the visible encrypted the current attribute...
                if (!leftChildProfile.getVisibleEncrypted().contains(currentAttribute))
                {
                    // If the child profile contains in the visible encrypted the current attribute
                    if (leftChildProfile.getVisibleEncrypted().contains(currentAttribute))
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
    }

    // Update the profile with encryption and decryption (if needed) for the operation Join
    private void update2Children(RelationProfile currentProfile, RelationProfile leftChildProfile, RelationProfile rightChildProfile, RelationProfile updatedProfile, Provider currentProvider) {

        // For all the currentProfile's visible plaintext attributes ...
        for (int i = 0; i < currentProfile.getVisiblePlaintext().size(); i++) {
            String currentAttribute = currentProfile.getVisiblePlaintext().get(i);

            // If the current attribute visibility is Plaintext for the current provider...
            if (AuthorizationModel.checkVisibility(currentProvider, currentAttribute, "Plaintext")) {
                // If the left child profile doesn't contain in the visible plaintext the current attribute...
                if (!leftChildProfile.getVisiblePlaintext().contains(currentAttribute)) {
                    // If the left child profile contains in the visible encrypted the current attribute
                    if (leftChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                        // Update the relation profile moving the attribute from the visible plaintext
                        // to the visible encrypted
                        updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                        updatedProfile.getVisibleEncrypted().add(currentAttribute);
                    } else {
                        // If the right child profile doesn't contain in the visible plaintext the current attribute...
                        if (!rightChildProfile.getVisiblePlaintext().contains(currentAttribute)) {
                            // If the right child profile contains in the visible encrypted the current attribute
                            if (rightChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                                // Update the relation profile moving the attribute from the visible plaintext
                                // to the visible encrypted
                                updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                                updatedProfile.getVisibleEncrypted().add(currentAttribute);
                            }
                        } else
                            System.out.println("CostModel.UpdateRelationProfile: ERROR the attribute is not visible (visibility#1)");
                    }
                }
            }
            // The current attribute visibility is Encrypted for currentProvider
            else {
                // If the left child profile doesn't contain in the visible encrypted the current attribute...
                if (!leftChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                    // If the left child profile contains in the visible plaintext the current attribute
                    if (leftChildProfile.getVisiblePlaintext().contains(currentAttribute)) {
                        // Update the relation profile moving the attribute from the visible plaintext
                        // to the visible encrypted
                        updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                        updatedProfile.getVisibleEncrypted().add(currentAttribute);
                    } else {
                        // If the right child profile doesn't contain in the visible encrypted the current attribute...
                        if (!rightChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                            // If the right child profile contains in the visible plaintext the current attribute
                            if (rightChildProfile.getVisiblePlaintext().contains(currentAttribute)) {
                                // Update the relation profile moving the attribute from the visible plaintext
                                // to the visible encrypted
                                updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                                updatedProfile.getVisibleEncrypted().add(currentAttribute);
                            } else
                                System.out.println("CostModel.UpdateRelationProfile: ERROR the attribute is not visible (visibility#2)");
                        }
                    }
                }
            }
        }

        // For all the currentProfile's visible encrypted attributes ...
        for (int i = 0; i < currentProfile.getVisibleEncrypted().size(); i++) {
            String currentAttribute = currentProfile.getVisibleEncrypted().get(i);

            // If the current attribute visibility is Encrypted for the current provider ...
            if (AuthorizationModel.checkVisibility(currentProvider, currentAttribute, "Encrypted")) {
                // If the left child profile doesn't contain in the visible encrypted the current attribute...
                if (!leftChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                    // If the left child profile contains in the visible plaintext the current attribute
                    if (leftChildProfile.getVisiblePlaintext().contains(currentAttribute)) {
                        // Update the relation profile moving the attribute from the visible encrypted
                        // to the visible plaintext
                        updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                        updatedProfile.getVisiblePlaintext().add(currentAttribute);
                    } else {
                        // If the right child profile doesn't contain in the visible encrypted the current attribute...
                        if (!rightChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                            // If the right child profile contains in the visible plaintext the current attribute
                            if (rightChildProfile.getVisiblePlaintext().contains(currentAttribute)) {
                                // Update the relation profile moving the attribute from the visible encrypted
                                // to the visible plaintext
                                updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                                updatedProfile.getVisiblePlaintext().add(currentAttribute);
                            } else
                                System.out.println("CostModel.UpdateRelationProfile: ERROR invalid attribute (visibility#3)");
                        }
                    }
                }
                // The current attribute visibility is Plaintext for the current provider
                else {
                    // If the left child profile doesn't contain in the visible encrypted the current attribute...
                    if (!leftChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                        // If the left child profile contains in the visible encrypted the current attribute
                        if (leftChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                            // Update the relation profile moving the attribute from the visible encrypted
                            // to the visible plaintext
                            updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                            updatedProfile.getVisiblePlaintext().add(currentAttribute);
                        } else {
                            // If the right child profile doesn't contain in the visible encrypted the current attribute...
                            if (!rightChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                                // If the right child profile contains in the visible encrypted the current attribute
                                if (rightChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                                    // Update the relation profile moving the attribute from the visible encrypted
                                    // to the visible plaintext
                                    updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                                    updatedProfile.getVisiblePlaintext().add(currentAttribute);
                                } else
                                    System.out.println("CostModel.UpdateRelationProfile: ERROR invalid attribute (visibility#4)");
                            }
                        }
                    }
                }
            }
        }
    }

    // ************************************************************************
    // Cost Computation

    // TODO private (public only for Testing)
    public double computeCost(Provider operationProvider, Provider childProvider, BinaryNode<Relation> relationNode)
    {
        // Dimensions in Giga Bytes
        double totalGB = relationNode.getElement().getSyzeInBytes() * Math.pow(10, -9);

        // Represents the single operation cost
        // [ $ ]
        double operationCost = getOperationCost(operationProvider, totalGB, relationNode.getElement().getOperation());

        // Represents the proportion (encrypted attributes / total attributes)
        double encryptionPercent = relationNode.getElement().getRelationProfile().getVisibleEncrypted().size() / (relationNode.getElement().getRelationProfile().getVisiblePlaintext().size() + relationNode.getElement().getRelationProfile().getVisibleEncrypted().size());

        // Represents the encryption cost ( ( bytes encrypted / (cpu speed * encryption overhead)) *  cpu cost)
        // [ $ ]
        double encryptionCost = ((totalGB * encryptionPercent) / (operationProvider.getCosts().getCpuSpeed() * operationProvider.getCosts().getEncryption())) * operationProvider.getCosts().getCpu();

        // Represent the transfer cost from children to father
        double transferCost = totalGB * findCostPerGB(operationProvider, childProvider);

        return (encryptionCost + transferCost + operationCost);
    }

    private double findCostPerGB(Provider operationProvider, Provider childProvider)
    {
        List<String> linksName = operationProvider.getLinks().getName();
        int index = linksName.indexOf(childProvider.getName());

        // return the right cost per GB
        return operationProvider.getLinks().getCostPerGB().get(index);
    }

    private double getOperationCost(Provider operationProvider, double totalGB, String operationType)
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

        return ((totalGB / (operationProvider.getCosts().getCpuSpeed() * operationCost)) * operationProvider.getCosts().getCpu());
    }

}
