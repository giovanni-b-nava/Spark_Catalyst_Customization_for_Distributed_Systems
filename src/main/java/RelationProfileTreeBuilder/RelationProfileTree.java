package RelationProfileTreeBuilder;


import TreeStructure.BinaryNode;
import TreeStructure.BinaryTree;
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Spark on 13/11/2016.
 */
public class RelationProfileTree {

    // Tree representing the relations in the optimized plan
    public BinaryTree<Relation> relationTree;

    // Builds the relationTree with the relations representing the operations
    public void buildTree(LogicalPlan plan) {

        // Generate the root of the relationTree
        Relation e = this.createRelation(plan);
        BinaryNode<Relation> root = new BinaryNode<>(e);
        relationTree = new BinaryTree<>(root);

        // Generate the rest of the relationTree
        this.generateNodes(plan.apply(0), root);

        // Complete the tree with the profile of each operation
        this.generateProfiles(relationTree.getRoot());
    }

    // Generate the relation of the current level
    private Relation createRelation(LogicalPlan plan) {
        String operation = plan.nodeName();
        List e = this.collectAttributes(plan);
        return new Relation(operation, e);
    }

    // Generates the list of attributes for the current operation
    private List<String> collectAttributes(LogicalPlan plan) {

        List<String> l = new ArrayList<>();

        switch(plan.nodeName()) {
            //TODO migliorare la generazione di join e filter (non vedono gli operatori coinvolti)
            case "Project":
            case "Aggregate":
                int i = 0;
                while(i < plan.expressions().toList().length()) {
                    String s = plan.expressions().apply(i).toString();
                    l.add(s);
                    i++;
                }
                break;
            case "Join":
            case "Filter":
                int x=0;
                while(x < plan.apply(0).constraints().toList().size()) {
                    String s;
                    if(plan.apply(0).constraints().toList().apply(x).flatArguments().toList().size() == 1) {
                        s = plan.apply(0).constraints().toList().apply(x).flatArguments().toList().apply(0).toString();
                        l.add(s);
                    } else {
                        s = plan.apply(0).constraints().toList().apply(x).prettyName();
                        l.add(s);
                    }
                    x++;
                }
                break;
            case "LogicalRelation":
                int f = 0;
                while(f < plan.output().size()) {
                    String s = String.valueOf(plan.output().apply(f));
                    l.add(s);
                    f++;
                }
                break;
            default:
                System.out.println("default");
        }
        return l;
    }

    // Recursively generate all the nodes in the relationTree
    private void generateNodes(LogicalPlan plan, BinaryNode<Relation> father) {

        if(plan.children().size() == 1) {
            Relation r = this.createRelation(plan.children().toList().apply(0));
            BinaryNode n = new BinaryNode(r);
            father.setLeft(n);
            n.setFather(father);
            this.generateNodes(plan.children().toList().apply(0), n);
        }
        else if(plan.children().size() == 2) {
            Relation r1 = this.createRelation(plan.children().toList().apply(0));
            BinaryNode n1 = new BinaryNode(r1);
            father.setLeft(n1);
            n1.setFather(father);
            this.generateNodes(plan.children().toList().apply(0), n1);
            Relation r2 = this.createRelation(plan.children().toList().apply(1));
            BinaryNode n2 = new BinaryNode(r2);
            father.setRight(n2);
            n2.setFather(father);
            this.generateNodes(plan.children().toList().apply(1), n2);
        }
    }

    // Generate the profile for each node with a post order visit
    private void generateProfiles(BinaryNode<Relation> node) {
        if (node == null) return;
        generateProfiles(node.getLeft());
        generateProfiles(node.getRight());
        setProfile(node);
        return;
    }

    //TODO generare il profilo in base al tipo di operazione ed ai profili dei figli
    private void setProfile(BinaryNode<Relation> node) {
    }

    // Generate the specific profile for each operation
    private RelationProfile setProfile(BinaryNode<Relation> node, RelationProfile profile) {
        //TODO modificare il profilo (per il join servono due relazioni e per filter bisogna scandire le operazioni)
        RelationProfile p = new RelationProfile(null, null, null, null, null);

        switch (node.getElement().getOperation()) {
            case "Aggregate":
                p.setVisiblePlaintext(node.getElement().getAttributes());
                p.setVisibleEncrypted(profile.getVisibleEncrypted());
                for(int i = 0; i < profile.getVisiblePlaintext().size(); i++) {
                    if(p.getVisiblePlaintext().contains(profile.getVisiblePlaintext().get(i))) {
                        p.getImplicitPlaintext().add(profile.getVisiblePlaintext().get(i));
                    }
                }
                p.setImplicitEncrypted(profile.getImplicitEncrypted());
                p.setEquivalenceSets(profile.getEquivalenceSets());
                break;
            case "Project":
                p.setVisiblePlaintext(node.getElement().getAttributes());
                p.setVisibleEncrypted(profile.getVisibleEncrypted());
                p.setImplicitPlaintext(profile.getImplicitPlaintext());
                p.setImplicitEncrypted(profile.getImplicitEncrypted());
                p.setEquivalenceSets(profile.getEquivalenceSets());
                break;
            case "Filter":
                break;
            case "Join":

                break;
            default:
                System.out.println("LogicalRelation or unknown");
        }
        return p;
    }
}
