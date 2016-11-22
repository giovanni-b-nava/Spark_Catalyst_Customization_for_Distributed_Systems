package AuthorizationModel;

import ConfigurationParser.Node;
import RelationProfileTreeBuilder.Relation;
import RelationProfileTreeBuilder.RelationProfile;
import TreeStructure.BinaryNode;
import TreeStructure.BinaryTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spark on 17/11/2016.
 */
public class AuthorizationModel {

    public BinaryTree<List<Node>> subjectTree;

    // Generate the list of subjects authorized to execute each operation and put them in a tree
    public void buildSubjectTree(List<Node> nodes, BinaryTree<Relation> profileTree) {

        // Set identification numbers for the attributes of the nodes
        List<Node> indexed = this.setIndexNodes(nodes, profileTree);

        // Generate the root of the subjectTree
        List<Node> n = this.authorizedSubjects(indexed, profileTree.getRoot());
        BinaryNode<List<Node>> root = new BinaryNode<>(n);
        subjectTree = new BinaryTree<>(root);

        // Generate the rest of the subjectTree
        this.generateNodes(indexed, root, profileTree.getRoot());
    }

    //TODO spostare i metodi di supporto in classi apposite
    // GENERAZIONE INDICI

    // Copy the same indexes of the tree in the attributes of the node's tables
    private List<Node> setIndexNodes(List<Node> nodes, BinaryTree<Relation> profileTree) {
        List<Node> indexed = nodes;
        // Create the list of logical relations
        List<Relation> logicalRelations = new ArrayList<>();
        List<Relation> treeNodes = profileTree.DFSVisit();
        for (int i = 0; i < treeNodes.size(); i++) {
            if (treeNodes.get(i).getOperation().equals("LogicalRelation")) {
                logicalRelations.add(treeNodes.get(i));
            }
        }
        for (int x = 0; x < nodes.size(); x++) {
            for (int y = 0; y < nodes.get(x).getTables().size(); y++) {
                for (int z = 0; z < logicalRelations.size(); z++) {
                    if (nodes.get(x).getTables().get(y).getName().equals(logicalRelations.get(z).getTableName())) {
                        for (int w = 0; w < logicalRelations.get(z).getAttributes().size(); w++) {
                            if (nodes.get(x).getTables().get(y).getPlaintext().contains(this.cleanAttribute(logicalRelations.get(z).getAttributes().get(w)))) {
                                int q = nodes.get(x).getTables().get(y).getPlaintext().indexOf(this.cleanAttribute(logicalRelations.get(z).getAttributes().get(w)));
                                indexed.get(x).getTables().get(y).getPlaintext().set(q, logicalRelations.get(z).getAttributes().get(w));
                            } else if (nodes.get(x).getTables().get(y).getEncrypted().contains(this.cleanAttribute(logicalRelations.get(z).getAttributes().get(w)))) {
                                int q = nodes.get(x).getTables().get(y).getEncrypted().indexOf(this.cleanAttribute(logicalRelations.get(z).getAttributes().get(w)));
                                indexed.get(x).getTables().get(y).getEncrypted().set(q, logicalRelations.get(z).getAttributes().get(w));
                            }
                        }
                    }
                }
            }
        }
        return indexed;
    }

    //TODO metodo duplicato
    // Cut the index from the attribute name
    private String cleanAttribute(String s) {
        CharSequence c = s.subSequence(0, s.indexOf("#"));
        String r = c.toString();
        return r;
    }

    // SOGGETTI AUTORIZZATI

    // Generate the list of authorized nodes for the current operation
    private List<Node> authorizedSubjects(List<Node> nodes, BinaryNode<Relation> node) {
        List<Node> n = new ArrayList<>();
        for(int i=0; i < nodes.size(); i++) {
            if(!node.getElement().getOperation().equals("LogicalRelation")) {
                if (this.isAuthorized(nodes.get(i), node.getElement().getProfile())) {
                    n.add(nodes.get(i));
                }
            }
        }
        return n;
    }

    // Return true if the node is authorized to execute the operation
    private boolean isAuthorized(Node node, RelationProfile profile) {

        boolean authorized = false;

        // Check if node is authorized for visible plaintext
        if(profile.getVisiblePlaintext() != null) {
            for (int i = 0; i < profile.getVisiblePlaintext().size(); i++) {
                authorized = this.checkVisibility(node, profile.getVisiblePlaintext().get(i), "Plaintext");
                if(!authorized)
                    return false;
            }
        } else authorized = true;

        // Check if node is authorized for implicit plaintext
        if(authorized) {
            if(profile.getImplicitPlaintext() != null) {
                for (int i = 0; i < profile.getImplicitPlaintext().size(); i++) {
                    authorized = this.checkVisibility(node, profile.getImplicitPlaintext().get(i), "Plaintext");
                    if(!authorized)
                        return false;
                }
            } else authorized = true;
        } else return false;

        // Check if node is authorized for visible encrypted
        if(authorized) {
            if(profile.getVisibleEncrypted() != null) {
                for (int i = 0; i < profile.getVisibleEncrypted().size(); i++) {
                    authorized = this.checkVisibility(node, profile.getVisibleEncrypted().get(i), "Encrypted");
                    if(!authorized)
                        return false;
                }
            } else authorized = true;
        } else return false;

        // Check if node is authorized for implicit encrypted
        if(authorized) {
            if(profile.getImplicitEncrypted() != null) {
                for (int i = 0; i < profile.getImplicitEncrypted().size(); i++) {
                    authorized = this.checkVisibility(node, profile.getImplicitEncrypted().get(i), "Encrypted");
                    if(!authorized)
                        return false;
                }
            } else authorized = true;
        } else return false;

        // Check if the equivalence lists have the same visibility
        if(authorized) {
            if(profile.getEquivalenceSets() != null) {
                for(int i = 0; i < profile.getEquivalenceSets().size(); i++) {
                    boolean plain1 = this.checkVisibility(node, profile.getEquivalenceSets().get(i).get(0), "Plaintext");
                    boolean plain2 = this.checkVisibility(node, profile.getEquivalenceSets().get(i).get(1), "Plaintext");
                    boolean enc1 = this.checkVisibility(node, profile.getEquivalenceSets().get(i).get(0), "Encrypted");
                    boolean enc2 = this.checkVisibility(node, profile.getEquivalenceSets().get(i).get(1), "Encrypted");
                    if(!(plain1 && plain2) && !(enc1 && enc2))
                        return false;
                }
            } else authorized = true;
        } else return false;

        return authorized;
    }

    // Check if the current attribute has the right visibility
    private boolean checkVisibility(Node node, String attribute, String visibility) {
        for(int x = 0; x < node.getTables().size(); x++) {
            if(visibility.equals("Plaintext")) {
                if(node.getTables().get(x).getPlaintext().contains(attribute)) {
                    return true;
                }
            }
            else if(visibility.equals("Encrypted")) {
                if (node.getTables().get(x).getPlaintext().contains(attribute) || node.getTables().get(x).getEncrypted().contains(attribute)) {
                    return true;
                }
            }
            else System.out.println("Error: wrong visibility");
        }
        return false;
    }

    // GENERAZIONE NODI

    // Recursively generate all the nodes in the subjectTree
    private void generateNodes(List<Node> nodes, BinaryNode<List<Node>> father, BinaryNode<Relation> relation) {
        if(relation.getLeft() != null && relation.getRight() == null) {
            List<Node> l = this.authorizedSubjects(nodes, relation.getLeft());
            BinaryNode n = new BinaryNode(l);
            father.setLeft(n);
            n.setFather(father);
            this.generateNodes(nodes, n, relation.getLeft());
        }
        else if(relation.getLeft() != null && relation.getRight() != null) {
            List<Node> l1 = this.authorizedSubjects(nodes, relation.getLeft());
            BinaryNode n1 = new BinaryNode(l1);
            father.setLeft(n1);
            n1.setFather(father);
            this.generateNodes(nodes, n1, relation.getLeft());
            List<Node> l2 = this.authorizedSubjects(nodes, relation.getRight());
            BinaryNode n2 = new BinaryNode(l2);
            father.setRight(n2);
            n2.setFather(father);
            this.generateNodes(nodes, n2, relation.getRight());
        }
    }
}
