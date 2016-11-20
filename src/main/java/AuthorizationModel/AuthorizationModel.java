package AuthorizationModel;

import ParserConfigurator.Node;
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
    public void builSubjectTree(List<Node> nodes, BinaryTree<Relation> profileTree) {

        // Generate the root of the subjectTree
        List<Node> n = this.authorizedSubjects(nodes, profileTree.getRoot());
        BinaryNode<List<Node>> root = new BinaryNode<>(n);
        subjectTree = new BinaryTree<>(root);

        // Generate the rest of the subjectTree
        this.generateNodes(nodes, root, profileTree.getRoot());
    }

    // Generate the list of authorized nodes for the current operation
    private List<Node> authorizedSubjects(List<Node> nodes, BinaryNode<Relation> node) {
        List<Node> n = new ArrayList<>();
        for(int i=0; i < nodes.size(); i++) {
            if(this.isAuthorized(nodes.get(i), node.getElement().getProfile())) {
                n.add(nodes.get(i));
            }
        }
        return n;
    }

    // Return true if the node is authorized to execute the operation
    private boolean isAuthorized(Node node, RelationProfile profile) {
        //TODO implementare le tre regole per verificare se un soggetto è autorizzato
        return true;
    }

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
