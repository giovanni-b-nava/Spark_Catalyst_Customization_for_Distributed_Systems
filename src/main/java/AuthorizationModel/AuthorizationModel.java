package AuthorizationModel;

import ConfigurationParser.Provider;
import RelationProfileTreeBuilder.Relation;
import RelationProfileTreeBuilder.RelationProfile;
import RelationProfileTreeBuilder.RelationProfileTree;
import TreeStructure.BinaryNode;
import TreeStructure.BinaryTree;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationModel
{

    private BinaryTree<List<Provider>> subjectTree;

    // Generate the list of subjects authorized to execute each operation and put them in a tree
    public AuthorizationModel(List<Provider> providers, BinaryTree<Relation> profileTree)
    {
        // Set the identification numbers for all the providers' attributes
        List<Provider> indexed = this.setIndexNodes(providers, profileTree);

        // Generate the root of the subjectTree
        List<Provider> n = this.authorizedSubjects(indexed, profileTree.getRoot());
        BinaryNode<List<Provider>> root = new BinaryNode<>(n);
        subjectTree = new BinaryTree<>(root);

        // Generate the rest of the subjectTree
        this.generateNodes(indexed, root, profileTree.getRoot());
    }

    public BinaryTree<List<Provider>> getSubjectTree()
    {
        return subjectTree;
    }

    // Copy the same indexes of the tree in the attributes of the node's tables
    private List<Provider> setIndexNodes(List<Provider> providers, BinaryTree<Relation> profileTree)
    {
        List<Provider> indexed = providers;

        // Create the list of logical relations
        List<Relation> logicalRelations = new ArrayList<>();
        List<Relation> treeNodes = profileTree.DFSVisit();

        for (int i = 0; i < treeNodes.size(); i++)
        {
            if (treeNodes.get(i).getOperation().equals("LogicalRelation"))
            {
                logicalRelations.add(treeNodes.get(i));
            }
        }

        for (int x = 0; x < providers.size(); x++)
        {
            for (int y = 0; y < providers.get(x).getTables().size(); y++)
            {
                for (int z = 0; z < logicalRelations.size(); z++)
                {
                    if (providers.get(x).getTables().get(y).getName().equals(logicalRelations.get(z).getTableName())) {
                        for (int w = 0; w < logicalRelations.get(z).getAttributes().size(); w++)
                        {
                            if (providers.get(x).getTables().get(y).getPlaintext().contains(RelationProfileTree.cleanAttribute(logicalRelations.get(z).getAttributes().get(w))))
                            {
                                int q = providers.get(x).getTables().get(y).getPlaintext().indexOf(RelationProfileTree.cleanAttribute(logicalRelations.get(z).getAttributes().get(w)));
                                indexed.get(x).getTables().get(y).getPlaintext().set(q, logicalRelations.get(z).getAttributes().get(w));
                            } else if (providers.get(x).getTables().get(y).getEncrypted().contains(RelationProfileTree.cleanAttribute(logicalRelations.get(z).getAttributes().get(w)))) {
                                int q = providers.get(x).getTables().get(y).getEncrypted().indexOf(RelationProfileTree.cleanAttribute(logicalRelations.get(z).getAttributes().get(w)));
                                indexed.get(x).getTables().get(y).getEncrypted().set(q, logicalRelations.get(z).getAttributes().get(w));
                            }
                        }
                    }
                }
            }
        }

        // Update the original providers with the Spark Indexes
        providers = indexed;

        return indexed;
    }

    // Generate the list of authorized providers for the current operation
    private List<Provider> authorizedSubjects(List<Provider> providers, BinaryNode<Relation> node)
    {
        List<Provider> authorized = new ArrayList<>();

        for(int i = 0; i < providers.size(); i++)
        {
            // NOT a LogicalRelation
            if(!node.getElement().getOperation().equals("LogicalRelation"))
            {
                if (this.isAuthorized(providers.get(i), node.getElement().getRelationProfile()))
                {
                    authorized.add(providers.get(i));
                }
            }
            // LogicalRelation
            else if(providers.get(i).getCategory().equals("storage_server"))
            {
                authorized.add(providers.get(i));
            }
        }

        return authorized;
    }

    // Return true if the provider is authorized to execute the operation
    private boolean isAuthorized(Provider provider, RelationProfile profile)
    {

        boolean authorized = false;

        // Check if provider is authorized for visible plaintext
        if(profile.getVisiblePlaintext() != null)
        {
            for (int i = 0; i < profile.getVisiblePlaintext().size(); i++)
            {
                authorized = this.checkVisibility(provider, profile.getVisiblePlaintext().get(i), "Plaintext");
                if(!authorized)
                    return false;
            }
        }
        else
            authorized = true;

        // Check if provider is authorized for implicit plaintext
        if(authorized)
        {
            if(profile.getImplicitPlaintext() != null)
            {
                for (int i = 0; i < profile.getImplicitPlaintext().size(); i++)
                {
                    authorized = this.checkVisibility(provider, profile.getImplicitPlaintext().get(i), "Plaintext");
                    if(!authorized)
                        return false;
                }
            }
            else
                authorized = true;
        }
        else
            return false;

        // Check if provider is authorized for visible encrypted
        if(authorized)
        {
            if(profile.getVisibleEncrypted() != null)
            {
                for (int i = 0; i < profile.getVisibleEncrypted().size(); i++)
                {
                    authorized = this.checkVisibility(provider, profile.getVisibleEncrypted().get(i), "Encrypted");
                    if(!authorized)
                        return false;
                }
            }
            else
                authorized = true;
        }
        else
            return false;

        // Check if provider is authorized for implicit encrypted
        if(authorized)
        {
            if(profile.getImplicitEncrypted() != null)
            {
                for (int i = 0; i < profile.getImplicitEncrypted().size(); i++)
                {
                    authorized = this.checkVisibility(provider, profile.getImplicitEncrypted().get(i), "Encrypted");
                    if(!authorized)
                        return false;
                }
            }
            else
                authorized = true;
        } else
            return false;

        // Check if the equivalence lists have the same visibility
        if(authorized)
        {
            if(profile.getEquivalenceSets() != null)
            {
                for(int i = 0; i < profile.getEquivalenceSets().size(); i++)
                {
                    boolean plain1 = this.checkVisibility(provider, profile.getEquivalenceSets().get(i).get(0), "Plaintext");
                    boolean plain2 = this.checkVisibility(provider, profile.getEquivalenceSets().get(i).get(1), "Plaintext");
                    boolean enc1 = this.checkVisibility(provider, profile.getEquivalenceSets().get(i).get(0), "Encrypted");
                    boolean enc2 = this.checkVisibility(provider, profile.getEquivalenceSets().get(i).get(1), "Encrypted");
                    if(!(plain1 && plain2) && !(enc1 && enc2))
                        return false;
                }
            }
            else
                authorized = true;
        }
        else
            return false;

        return authorized;
    }

    // Check if the current attribute has the right visibility
    public static boolean checkVisibility(Provider provider, String attribute, String visibility)
    {
        for(int x = 0; x < provider.getTables().size(); x++)
        {
            if(visibility.equals("Plaintext"))
            {
                if(provider.getTables().get(x).getPlaintext().contains(attribute))
                {
                    return true;
                }
            }
            else if(visibility.equals("Encrypted"))
            {
                if (provider.getTables().get(x).getPlaintext().contains(attribute) || provider.getTables().get(x).getEncrypted().contains(attribute))
                {
                    return true;
                }
            }
            else
                System.out.println("ERROR: Wrong Visibility!");
        }
        return false;
    }

    // Recursively generate all the providers in the subjectTree
    private void generateNodes(List<Provider> providers, BinaryNode<List<Provider>> father, BinaryNode<Relation> relation)
    {
        if(relation.getLeft() != null && relation.getRight() == null)
        {
            List<Provider> l = this.authorizedSubjects(providers, relation.getLeft());
            BinaryNode n = new BinaryNode(l);
            father.setLeft(n);
            n.setFather(father);
            this.generateNodes(providers, n, relation.getLeft());
        }
        else if(relation.getLeft() != null && relation.getRight() != null)
        {
            List<Provider> l1 = this.authorizedSubjects(providers, relation.getLeft());
            BinaryNode n1 = new BinaryNode(l1);
            father.setLeft(n1);
            n1.setFather(father);
            this.generateNodes(providers, n1, relation.getLeft());
            List<Provider> l2 = this.authorizedSubjects(providers, relation.getRight());
            BinaryNode n2 = new BinaryNode(l2);
            father.setRight(n2);
            n2.setFather(father);
            this.generateNodes(providers, n2, relation.getRight());
        }
    }
}