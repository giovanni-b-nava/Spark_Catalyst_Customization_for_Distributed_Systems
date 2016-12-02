package CostModel;

import AuthorizationModel.AuthorizationModel;
import ConfigurationParser.Provider;
import RelationProfileTreeBuilder.Relation;
import RelationProfileTreeBuilder.RelationProfile;
import RelationProfileTreeBuilder.RelationProfileTree;
import TreeStructure.BinaryNode;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Giovanni on 28/11/2016.
 */
public class CostModel
{

    private RelationProfileTree tree;
    private List<Provider> providers;

    public CostModel(List<Provider> providers, RelationProfileTree tree)
    {
        this.tree = tree;
        this.providers = providers;
    }

    public PlansMap generateOptimalPlans(BinaryNode<Relation> root)
    {
        // Base case: root = Logical Relation
        if (root.getLeft() == null && root.getRight() == null)
        {
            PlansMap leafMap = new PlansMap();
            Plan newPlan = new Plan();

            // 1. Set the BinaryNode<Relation>
            newPlan.setRelation(root);

            // 2. NO REQUIRE to updateRelationProfile

            // 3. Compute and assign Cost
            double cost = computeCost(findProvider("storage_server"), findProvider("storage_server"), root);
            newPlan.setCost(cost);
            newPlan.getAssignedProviders().add(findProvider("storage_server"));

            leafMap.addPlan(newPlan);
            return leafMap;
        }

        PlansMap leftPlansMap = generateOptimalPlans(root.getLeft());
        PlansMap rightPlansMap = null;

        if (root.getRight() != null)
            rightPlansMap = generateOptimalPlans(root.getRight());

        PlansMap plansMap = new PlansMap();

        if (root.getRight() == null)
        {
            // For all the providers...
            for (int i=0; i<providers.size(); i++)
            {
                // For all the left plans...
                for (int j=0; j<leftPlansMap.getPlansMap().size(); j++)
                {
                    // 1. GENERATE A NEW RELATION PROFILE
                    Plan leftChildPlan = findPlanIntoMap(leftPlansMap, j);
                    BinaryNode<Relation> leftChildRelation = leftChildPlan.getRelation();
                    BinaryNode<Relation> rootCopy = new BinaryNode<>(root);

                    // TODO Testing
                    System.out.println("root.RelationProfile: \n" + root.getElement().getRelationProfile());
                    System.out.println("rootCopy.RelationProfile: \n" + rootCopy.getElement().getRelationProfile());

                    rootCopy.setLeft(leftChildRelation);
                    tree.setProfile(rootCopy);
                    rootCopy.getElement().setRelationProfile(updateRelationProfile(providers.get(i), rootCopy));

                    // TODO Testing
                    System.out.println("provider  = " + i + " plan = " + j + "\n");
                    System.out.println("Plan:");
                    System.out.println(rootCopy.getElement().getRelationProfile());

                    // 2. COMPUTE THE COST
                    int leftChildProviderIndex = leftChildPlan.getAssignedProviders().size() - 1;
                    Provider childProvider = leftChildPlan.getAssignedProviders().get(leftChildProviderIndex);
                    double cost = computeCost(providers.get(i), childProvider, rootCopy) + leftChildPlan.getCost();

                    // 3. CREATE A NEW PLAN
                    Plan newPlan = new Plan();
                    newPlan.setRelation(rootCopy);
                    newPlan.setCost(cost);
                    newPlan.setAssignedProviders(leftChildPlan.getAssignedProviders());
                    newPlan.getAssignedProviders().add(childProvider);

                    // 4. ADD THE NEW PLAN TO PLANSMAP
                    plansMap.addPlan(newPlan);
                }
            }
        }
        // There is a right child
        else
        {
            // For all the providers...
            for (int i=0; i<providers.size(); i++)
            {
                // For all the left plans...
                for (int j=0; j<leftPlansMap.getPlansMap().size(); j++)
                {
                    // For all the right plans...
                    for (int k=0; k<rightPlansMap.getPlansMap().size(); k++)
                    {
                        // TODO
                    }
                }
            }
        }

        return plansMap;
    }

    // Find the Provider which matches category
    private Provider findProvider(String category)
    {
        Provider provider = new Provider();

        for (int i=0; i<providers.size(); i++)
        {
            if (providers.get(i).getCategory().equals(category))
            {
                provider = providers.get(i);
            }
        }

        return provider;
    }

    // Find the plan of the current element of PlansMap
    private Plan findPlanIntoMap(PlansMap plansMap, int i)
    {
        Set keySet = plansMap.getPlansMap().keySet();
        Iterator iterator = keySet.iterator();
        Plan value = new Plan();

        for (int j=0; j <= i; j++)
        {
            if (iterator.hasNext())
            {
                Object key = iterator.next();
                value = plansMap.getPlansMap().get(key);
            }
        }

        return value;
    }

    // Generate the updated profile updating (if needed) Encryption or Decryption BEFORE computing the cost
    private RelationProfile updateRelationProfile(Provider currentProvider, BinaryNode<Relation> relationNode)
    {
        RelationProfile currentProfile = relationNode.getElement().getRelationProfile();
        RelationProfile updatedProfile = new RelationProfile(currentProfile);

        // Filter, Project, Aggregate
        if (relationNode.getLeft() != null && relationNode.getRight() == null)
        {
            RelationProfile leftChildProfile = relationNode.getLeft().getElement().getRelationProfile();
            updatedProfile = update1Child(currentProfile, leftChildProfile, currentProvider);
        }
        // Join
        else if (relationNode.getLeft() != null && relationNode.getRight() != null)
        {
            RelationProfile leftChildProfile = relationNode.getLeft().getElement().getRelationProfile();
            RelationProfile rightChildProfile = relationNode.getRight().getElement().getRelationProfile();
            updatedProfile = update2Children(currentProfile, leftChildProfile, rightChildProfile, currentProvider);

            // Check if the attributes of the Join have the same visibility
            String firstAttribute = relationNode.getElement().getAttributes().get(0);
            String secondAttribute = relationNode.getElement().getAttributes().get(1);
            // ASSUMPTION: if one of the two attributes is in plaintext then decrypt the other
            if(updatedProfile.getVisiblePlaintext().contains(firstAttribute) && !updatedProfile.getVisiblePlaintext().contains(secondAttribute)) {
                // Update the relation profile (current) moving the second attribute from the visible encrypted
                // to the visible plaintext
                updatedProfile.getVisiblePlaintext().remove(secondAttribute);
                updatedProfile.getVisibleEncrypted().add(secondAttribute);
            }
            else if(!updatedProfile.getVisiblePlaintext().contains(firstAttribute) && updatedProfile.getVisiblePlaintext().contains(secondAttribute)) {
                // Update the relation profile (current) moving the first attribute from the visible encrypted
                // to the visible plaintext
                updatedProfile.getVisiblePlaintext().remove(firstAttribute);
                updatedProfile.getVisibleEncrypted().add(firstAttribute);
            }
        }
        // else: for a Logical Relation don't do anything

        return updatedProfile;
    }

    // Update the profile with encryption and decryption (if needed) for the operations Filter, Project, Aggregate
    private RelationProfile update1Child(RelationProfile currentProfile, RelationProfile leftChildProfile, Provider currentProvider)
    {
        RelationProfile updatedProfile = new RelationProfile(currentProfile);

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
                        // Update the relation profile (current) moving the attribute from the visible plaintext
                        // to the visible encrypted
                        updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                        updatedProfile.getVisibleEncrypted().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.update1Child: ERROR the attribute is not visible (visibility#1)");
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
                        // Update the relation profile (current) moving the attribute from the visible plaintext
                        // to the visible encrypted
                        updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                        updatedProfile.getVisibleEncrypted().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.update1Child: ERROR the attribute is not visible (visibility#2)");
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
                        // Update the relation profile (current) moving the attribute from the visible encrypted
                        // to the visible plaintext
                        updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                        updatedProfile.getVisiblePlaintext().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.update1Child: ERROR invalid attribute (visibility#3)");
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
                        // Update the relation profile (current) moving the attribute from the visible encrypted
                        // to the visible plaintext
                        updatedProfile.getVisibleEncrypted().remove(currentAttribute);
                        updatedProfile.getVisiblePlaintext().add(currentAttribute);
                    }
                    else
                        System.out.println("CostModel.update1Child: ERROR invalid attribute (visibility#4)");
                }
            }
        }

        return updatedProfile;
    }

    // Update the profile with encryption and decryption (if needed) for the operation Join
    private RelationProfile update2Children(RelationProfile currentProfile, RelationProfile leftChildProfile, RelationProfile rightChildProfile, Provider currentProvider)
    {

        RelationProfile updatedProfile = new RelationProfile(currentProfile);

        // For all the currentProfile's visible plaintext attributes ...
        for (int i = 0; i < currentProfile.getVisiblePlaintext().size(); i++)
        {
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
                            // If the right child profile (current) contains in the visible encrypted the current attribute
                            if (rightChildProfile.getVisibleEncrypted().contains(currentAttribute)) {
                                // Update the relation profile (current) moving the attribute from the visible plaintext
                                // to the visible encrypted
                                updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                                updatedProfile.getVisibleEncrypted().add(currentAttribute);
                            }
                        } else
                            System.out.println("CostModel.update2Children: ERROR the attribute is not visible (visibility#1)");
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
                            // If the right child profile (current) contains in the visible plaintext the current attribute
                            if (rightChildProfile.getVisiblePlaintext().contains(currentAttribute)) {
                                // Update the relation profile (current) moving the attribute from the visible plaintext
                                // to the visible encrypted
                                updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                                updatedProfile.getVisibleEncrypted().add(currentAttribute);
                            } else
                                System.out.println("CostModel.update2Children: ERROR the attribute is not visible (visibility#2)");
                        }
                    }
                }
            }
        }

        // For all the currentProfile's visible encrypted attributes ...
        for (int i = 0; i < currentProfile.getVisibleEncrypted().size(); i++)
        {
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
                                System.out.println("CostModel.update2Children: ERROR invalid attribute (visibility#3)");
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
                                    System.out.println("CostModel.update2Children: ERROR invalid attribute (visibility#4)");
                            }
                        }
                    }
                }
            }
        }

        return updatedProfile;
    }

    // ************************************************************************
    // COST COMPUTATION

    private double computeCost(Provider operationProvider, Provider childProvider, BinaryNode<Relation> relationNode)
    {
        // Dimensions in Giga Bytes
        double totalGB = relationNode.getElement().getSyzeInBytes() * Math.pow(10, -9);

        // Represents the single operation cost
        // [ $ ]
        double operationCost = getOperationCost(operationProvider, totalGB, relationNode.getElement().getOperation());

        // Represents the proportion (encrypted attributes / total attributes)
        double encryptionPercent;

        if ((relationNode.getElement().getRelationProfile().getVisiblePlaintext().size() + relationNode.getElement().getRelationProfile().getVisibleEncrypted().size() == 0))
            encryptionPercent = 0;
        else
            encryptionPercent = relationNode.getElement().getRelationProfile().getVisibleEncrypted().size() / (relationNode.getElement().getRelationProfile().getVisiblePlaintext().size() + relationNode.getElement().getRelationProfile().getVisibleEncrypted().size());

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
            case "LogicalRelation" :
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
                operationCost = 1;
                System.out.println("CostModel.getOperationCost: ERROR Unknown operation!");
        }

        return ((totalGB / (operationProvider.getCosts().getCpuSpeed() * operationCost)) * operationProvider.getCosts().getCpu());
    }

}
