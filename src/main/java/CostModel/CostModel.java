package CostModel;

import AuthorizationModel.AuthorizationModel;
import ConfigurationParser.Provider;
import ConfigurationParser.Table;
import DataConfigBuilder.DataBuilder;
import RelationProfileTreeBuilder.Relation;
import RelationProfileTreeBuilder.RelationProfile;
import RelationProfileTreeBuilder.RelationProfileTree;
import TreeStructure.BinaryNode;
import java.util.*;

public class CostModel
{

    private RelationProfileTree tree;
    private List<Provider> providers;

    public CostModel(List<Provider> providers, RelationProfileTree tree)
    {
        this.tree = tree;
        this.providers = providers;
    }

    // TODO Metodo per Testing
    private void printNewPlan(Plan newPlan)
    {
        //System.out.println("newPlan = " + newPlan.toString());
    }

    // Returns the plan with the lowest cost
    public Plan getOptimalPlan(PlansMap plansMap)
    {
        List<Plan> plans = new ArrayList<>();
        // Put all the plans in a list
        for (int i=0; i<plansMap.getPlansMap().size(); i++)
        {
            Plan plan = findPlanIntoMap(plansMap, i);
            plans.add(plan);
        }
        // Order the list
        Collections.sort(plans);

        System.out.println("NUMBER OF PLANS = " + plans.size());

        return plans.get(0);
    }

    // Recursively generate all the plans that come from the combination of providers and operations and put them into a map
    public PlansMap generatePlans(BinaryNode<Relation> root, EncryptionProfile encProfile)
    {
        // BASE CASE: ROOT = Logical Relation
        if (root.getLeft() == null && root.getRight() == null)
        {
            PlansMap leafMap = new PlansMap();

            List<Provider> providers = DataBuilder.getDataBuilder().providers;
            String target = root.getElement().getTableName();

            // For all the providers...
            for (int i=0; i<providers.size(); i++)
            {
                // If the provider is a Storage Server...
                if (providers.get(i).getCategory().equals("storage_server"))
                {
                    BinaryNode<Relation> rootCopy = new BinaryNode<>(root);
                    RelationProfile profile = new RelationProfile();
                    Plan newPlan = new Plan();

                    // Get the table names
                    List<Table> tables = providers.get(i).getTables();

                    boolean targetFound = false;

                    for (int j = 0; j < tables.size(); j++)
                    {
                        // Assign the visibility to attributes of a table
                        if (target.equals(tables.get(j).getName()))
                        {
                            // Encrypted
                            for (int k=0; k < tables.get(j).getEncrypted().size(); k++)
                            {
                                profile.getVisibleEncrypted().add(tables.get(j).getEncrypted().get(k));
                            }
                            // Plaintext
                            for (int k=0; k < tables.get(j).getPlaintext().size(); k++)
                            {
                                profile.getVisiblePlaintext().add(tables.get(j).getPlaintext().get(k));
                            }
                            targetFound = true;
                        }
                    }
                    // If a Storage Server doesn't contain the target table => SKIP THE CREATION OF THIS PLAN!
                    if (targetFound == false)
                        continue;

                    // 1. SET THE BinaryNode<Relation>
                    newPlan.setRelation(rootCopy);

                    // 2. ASSIGN THE NEW RELATION PROFILE
                    rootCopy.getElement().setRelationProfile(profile);

                    // 3. COMPUTE AND ASSIGN COST
                    double cost = computeCost(providers.get(i), providers.get(i), null, rootCopy, encProfile);
                    newPlan.setCost(cost);
                    newPlan.getAssignedProviders().add(providers.get(i));

                    // 4. ADD THE NEW PLAN TO LEAFMAP
                    leafMap.addPlan(newPlan);

                    printNewPlan(newPlan);
                }
            }

            return leafMap;
        }

        // Create a new copy of the EncryptionProfile
        EncryptionProfile newEncProfile = new EncryptionProfile(encProfile);

        // Update the EncryptionProfile considering the current operation
        newEncProfile.updateEncryptionProfile(root.getElement());

        // GENERATION OF LEFT PLANS
        PlansMap leftPlansMap = generatePlans(root.getLeft(), newEncProfile);
        // GENERATION OF RIGHT PLANS
        PlansMap rightPlansMap = null;

        if (root.getRight() != null)
            rightPlansMap = generatePlans(root.getRight(), newEncProfile);

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

                    rootCopy.setLeft(leftChildRelation);

                    rootCopy.getElement().setRelationProfile(tree.buildOperationProfile(rootCopy));
                    rootCopy.getElement().setRelationProfile(updateRelationProfile(providers.get(i), rootCopy));

                    // 2. COMPUTE THE COST
                    int leftChildProviderIndex = leftChildPlan.getAssignedProviders().size() - 1;
                    Provider childProvider = leftChildPlan.getAssignedProviders().get(leftChildProviderIndex);
                    double cost = computeCost(providers.get(i), childProvider, null, rootCopy, encProfile) + leftChildPlan.getCost();

                    // 3. CREATE A NEW PLAN
                    Plan newPlan = new Plan();
                    newPlan.setRelation(rootCopy);
                    newPlan.setCost(cost);
                    newPlan.getAssignedProviders().addAll(leftChildPlan.getAssignedProviders());
                    newPlan.getAssignedProviders().add(providers.get(i));

                    // 4. ADD THE NEW PLAN TO PLANSMAP
                    plansMap.addPlan(newPlan);

                    printNewPlan(newPlan);
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
                        // 1. GENERATE A NEW RELATION PROFILE
                        Plan leftChildPlan = findPlanIntoMap(leftPlansMap, j);
                        Plan rightChildPlan = findPlanIntoMap(rightPlansMap, k);
                        BinaryNode<Relation> leftChildRelation = leftChildPlan.getRelation();
                        BinaryNode<Relation> rightChildRelation = rightChildPlan.getRelation();
                        BinaryNode<Relation> rootCopy = new BinaryNode<>(root);

                        rootCopy.setLeft(leftChildRelation);
                        rootCopy.setRight(rightChildRelation);

                        rootCopy.getElement().setRelationProfile(tree.buildOperationProfile(rootCopy));
                        rootCopy.getElement().setRelationProfile(updateRelationProfile(providers.get(i), rootCopy));

                        // 2. COMPUTE THE COST
                        int leftChildProviderIndex = leftChildPlan.getAssignedProviders().size() - 1;
                        int rightChildProviderIndex = rightChildPlan.getAssignedProviders().size() - 1;
                        Provider leftChildProvider = leftChildPlan.getAssignedProviders().get(leftChildProviderIndex);
                        Provider rightChildProvider = rightChildPlan.getAssignedProviders().get(rightChildProviderIndex);
                        double cost = computeCost(providers.get(i), leftChildProvider, rightChildProvider, rootCopy, encProfile) + leftChildPlan.getCost() + rightChildPlan.getCost();

                        // 3. CREATE A NEW PLAN
                        Plan newPlan = new Plan();
                        newPlan.setRelation(rootCopy);
                        newPlan.setCost(cost);
                        newPlan.getAssignedProviders().addAll(leftChildPlan.getAssignedProviders());
                        newPlan.getAssignedProviders().addAll(rightChildPlan.getAssignedProviders());
                        newPlan.getAssignedProviders().add(providers.get(i));

                        // 4. ADD THE NEW PLAN TO PLANSMAP
                        plansMap.addPlan(newPlan);

                        printNewPlan(newPlan);
                    }
                }
            }
        }

        return plansMap;
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
            updatedProfile = updateOneChild(currentProfile, leftChildProfile, currentProvider);
        }
        // Join
        else if (relationNode.getLeft() != null && relationNode.getRight() != null)
        {
            RelationProfile leftChildProfile = relationNode.getLeft().getElement().getRelationProfile();
            RelationProfile rightChildProfile = relationNode.getRight().getElement().getRelationProfile();
            updatedProfile = updateTwoChildren(currentProfile, leftChildProfile, rightChildProfile, currentProvider);

            // Check if the attributes of the Join have the same visibility
            String firstAttribute = relationNode.getElement().getAttributes().get(0);
            String secondAttribute = relationNode.getElement().getAttributes().get(1);
            // ASSUMPTION: if one of the two attributes is in plaintext then encrypt the other
            if(updatedProfile.getVisiblePlaintext().contains(firstAttribute) && !updatedProfile.getVisiblePlaintext().contains(secondAttribute)) {
                // Update the relation profile (current) moving the second attribute from the visible encrypted
                // to the visible plaintext
                updatedProfile.getVisiblePlaintext().remove(firstAttribute);
                updatedProfile.getVisibleEncrypted().add(firstAttribute);
            }
            else if(!updatedProfile.getVisiblePlaintext().contains(firstAttribute) && updatedProfile.getVisiblePlaintext().contains(secondAttribute)) {
                // Update the relation profile (current) moving the first attribute from the visible encrypted
                // to the visible plaintext
                updatedProfile.getVisiblePlaintext().remove(secondAttribute);
                updatedProfile.getVisibleEncrypted().add(secondAttribute);
            }
        }
        // else: for a Logical Relation don't do anything

        return updatedProfile;
    }

    // Update the profile with encryption and decryption (if needed) for the operations Filter, Project, Aggregate
    private RelationProfile updateOneChild(RelationProfile currentProfile, RelationProfile leftChildProfile, Provider currentProvider)
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
                        System.out.println("CostModel.updateOneChild: ERROR the attribute is not visible (visibility#1)");
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
                        System.out.println("CostModel.updateOneChild: ERROR the attribute is not visible (visibility#2)");
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
                        System.out.println("CostModel.updateOneChild: ERROR invalid attribute (visibility#3)");
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
                        System.out.println("CostModel.updateOneChild: ERROR invalid attribute (visibility#4)");
                }
            }
        }

        return updatedProfile;
    }

    // Update the profile with encryption and decryption (if needed) for the operation Join
    private RelationProfile updateTwoChildren(RelationProfile currentProfile, RelationProfile leftChildProfile, RelationProfile rightChildProfile, Provider currentProvider)
    {

        RelationProfile updatedProfile = new RelationProfile(currentProfile);

        // For all the currentProfile's visible plaintext attributes ...
        for (int i = 0; i < currentProfile.getVisiblePlaintext().size(); i++)
        {
            String currentAttribute = currentProfile.getVisiblePlaintext().get(i);

            // If the current attribute visibility is Plaintext for the current provider...
            if (AuthorizationModel.checkVisibility(currentProvider, currentAttribute, "Plaintext"))
            {
                // If the left child profile doesn't contain in the visible plaintext the current attribute...
                if (!leftChildProfile.getVisiblePlaintext().contains(currentAttribute))
                {
                    // If the left child profile contains in the visible encrypted the current attribute
                    if (leftChildProfile.getVisibleEncrypted().contains(currentAttribute))
                    {
                        // Update the relation profile moving the attribute from the visible plaintext
                        // to the visible encrypted
                        updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                        updatedProfile.getVisibleEncrypted().add(currentAttribute);
                    }
                    else
                    {
                        // If the right child profile doesn't contain in the visible plaintext the current attribute...
                        if (!rightChildProfile.getVisiblePlaintext().contains(currentAttribute))
                        {
                            // If the right child profile (current) contains in the visible encrypted the current attribute
                            if (rightChildProfile.getVisibleEncrypted().contains(currentAttribute))
                            {
                                // Update the relation profile (current) moving the attribute from the visible plaintext
                                // to the visible encrypted
                                updatedProfile.getVisiblePlaintext().remove(currentAttribute);
                                updatedProfile.getVisibleEncrypted().add(currentAttribute);
                            }
                        } else
                            System.out.println("CostModel.updateTwoChildren: ERROR the attribute is not visible (visibility#1)");
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
                                System.out.println("CostModel.updateTwoChildren: ERROR the attribute is not visible (visibility#2)");
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
                                System.out.println("CostModel.updateTwoChildren: ERROR invalid attribute (visibility#3)");
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
                                    System.out.println("CostModel.updateTwoChildren: ERROR invalid attribute (visibility#4)");
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

    private double computeCost(Provider operationProvider, Provider leftChildProvider, Provider rightChildProvider, BinaryNode<Relation> relationNode, EncryptionProfile encProfile)
    {
        // Dimensions in Giga Bytes
        double GB = relationNode.getElement().getSizeInBytes() * Math.pow(10, -9);
        double leftGB = 0;
        double rightGB = 0;

        // TODO GB TUNING
        GB = GB * Math.pow(10, 3);
        if(relationNode.getElement().getOperation().equals("LogicalRelation"))
        {
            System.out.println("Table GB = " + GB);
        }

        if (relationNode.getLeft() != null)
            leftGB = relationNode.getLeft().getElement().getSizeInBytes() * Math.pow(10, -9);
        if (relationNode.getRight() != null)
            rightGB = relationNode.getRight().getElement().getSizeInBytes() * Math.pow(10, -9);

        // Represents the single operation cost
        // [ $ ]
        double operationCost = getOperationCost(operationProvider, GB, relationNode.getElement().getOperation());

        // Represents the proportion (encrypted attributes / total attributes) of the children
        double encryptionPercentLeft = 0;
        double encryptionPercentRight = 0;
        double encryptionCostLeft = 0;
        double encryptionCostRight = 0;

        if (relationNode.getLeft() != null)
        {
            if ((relationNode.getLeft().getElement().getRelationProfile().getVisiblePlaintext().size() + relationNode.getLeft().getElement().getRelationProfile().getVisibleEncrypted().size() == 0))
            {
                encryptionPercentLeft = 0;
            }
            else
            {
                encryptionPercentLeft = relationNode.getElement().getRelationProfile().getVisibleEncrypted().size() / (relationNode.getElement().getRelationProfile().getVisiblePlaintext().size() + relationNode.getElement().getRelationProfile().getVisibleEncrypted().size());
            }

            // Select the LEFT encryption cost (AES or HOMOMORPHIC)
            double encProfileCost = 1;
            if (encryptionPercentLeft != 0)
            {
                List<String> supportedEncryption = encProfile.getMap().get(relationNode.getElement().getRelationProfile().getVisibleEncrypted().get(0));

                if (supportedEncryption.contains("homomorphic") && supportedEncryption.contains("aes"))
                {
                    encProfileCost = leftChildProvider.getCosts().getEncryptionAES();
                    System.out.println("[LEFT] AES");
                }
                else
                {
                    encProfileCost = leftChildProvider.getCosts().getEncryptionHOMOMORPHIC();
                    System.out.println("[LEFT] HOMOMORPHIC");
                }
            }
            // Represents the encryption cost ( ( bytes encrypted / (cpu speed * encryption overhead)) *  cpu cost)
            // [ $ ]
            encryptionCostLeft = ((leftGB * encryptionPercentLeft) / (leftChildProvider.getCosts().getCpuSpeed() * encProfileCost)) * leftChildProvider.getCosts().getCpu();
        }

        if (relationNode.getRight() != null)
        {
            if ((relationNode.getRight().getElement().getRelationProfile().getVisiblePlaintext().size() + relationNode.getRight().getElement().getRelationProfile().getVisibleEncrypted().size() == 0)) {
                encryptionPercentRight = 0;
            }
            else
            {
                encryptionPercentRight = relationNode.getElement().getRelationProfile().getVisibleEncrypted().size() / (relationNode.getElement().getRelationProfile().getVisiblePlaintext().size() + relationNode.getElement().getRelationProfile().getVisibleEncrypted().size());
            }

            // Select the RIGHT encryption cost (AES or HOMOMORPHIC)
            double encProfileCost = 1;
            if (encryptionPercentRight != 0)
            {
                List<String> supportedEncryption = encProfile.getMap().get(relationNode.getElement().getRelationProfile().getVisibleEncrypted().get(0));

                if (supportedEncryption.contains("homomorphic") && supportedEncryption.contains("aes"))
                {
                    encProfileCost = rightChildProvider.getCosts().getEncryptionAES();
                    System.out.println("[RIGHT] AES");
                }
                else
                {
                    encProfileCost = rightChildProvider.getCosts().getEncryptionHOMOMORPHIC();
                    System.out.println("[RIGHT] HOMOMORPHIC");
                }
            }

            // Represents the encryption cost ( ( bytes encrypted / (cpu speed * encryption overhead)) *  cpu cost)
            // [ $ ]
            encryptionCostRight = ((rightGB * encryptionPercentRight) / (rightChildProvider.getCosts().getCpuSpeed() * encProfileCost)) * rightChildProvider.getCosts().getCpu();
        }

        // Represent the transfer cost from children to father
        double transferCostLeft = 0;
        double transferCostRight = 0;

        if (leftChildProvider != null)
            transferCostLeft = leftGB * findCostPerGB(operationProvider, leftChildProvider);
        if (rightChildProvider != null)
            transferCostRight = rightGB * findCostPerGB(operationProvider, rightChildProvider);

        return (encryptionCostLeft + encryptionCostRight + transferCostLeft + transferCostRight + operationCost);
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
