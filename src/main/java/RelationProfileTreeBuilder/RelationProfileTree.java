package RelationProfileTreeBuilder;

import DataConfigBuilder.DataBuilder;
import TreeStructure.BinaryNode;
import TreeStructure.BinaryTree;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.catalyst.plans.logical.LogicalPlan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RelationProfileTree {

    // Tree representing the relations in the optimized plan
    private BinaryTree<Relation> relationTree;

    // Builds the relationTree with the relations representing the operations
    public RelationProfileTree(LogicalPlan plan) {

        // Generate the root of the relationTree
        Relation e = this.createRelation(plan);
        BinaryNode<Relation> root = new BinaryNode<>(e);
        relationTree = new BinaryTree<>(root);

        // Generate the rest of the relationTree
        this.generateChildren(plan.apply(0), root);

        // Complete the tree with the profile of each operation
        this.generateProfiles(relationTree.getRoot());
    }

    public BinaryTree<Relation> getRelationTree() {
        return relationTree;
    }

    // Generate the single relation of the current level
    private Relation createRelation(LogicalPlan plan) {
        String operation = plan.nodeName();
        long sizeInByte = plan.statistics().sizeInBytes().longValue();
        // Table
        if(operation.equals("LogicalRelation")) {
            List<String> e = this.collectAttributes(plan);
            String tableName = this.getTableName(e, DataBuilder.getDataBuilder().tables, DataBuilder.getDataBuilder().tableNames);
            return new Relation(operation, e, tableName, sizeInByte);
        }
        else // Operation
        {
            List<String> e = this.collectAttributes(plan);
            return new Relation(operation, e, sizeInByte);
        }
    }

    // Generates the list of attributes for the current operation
    private List<String> collectAttributes(LogicalPlan plan) {

        List<String> l = new ArrayList<>();

        switch(plan.nodeName()) {
            case "Project":
            case "Aggregate":
                int i = 0;
                while(i < plan.expressions().toList().length()) {
                    String s = plan.expressions().apply(i).toString();
                    l.add(s);
                    i++;
                }
                eliminateDuplicate(l);
                break;
            case "Join":
                int y=0;
                while(y < plan.apply(0).constraints().toList().size()) {
                    String s;
                    if (plan.apply(0).constraints().toList().apply(y).flatArguments().toList().size() == 1) {
                        s = plan.apply(0).constraints().toList().apply(y).flatArguments().toList().apply(0).toString();
                        l.add(s);
                    }
                    y++;
                }
                break;
            case "Filter":
                int x=0;
                while(x < plan.apply(0).constraints().toList().size()) {
                    String s;
                    if(plan.apply(0).constraints().toList().apply(x).flatArguments().toList().size() == 2) {
                        s = this.formatFilter(plan.apply(0).constraints().toList().apply(x).flatArguments().toList().apply(0).toString());
                        l.add(s);
                        s = this.formatFilter(plan.apply(0).constraints().toList().apply(x).flatArguments().toList().apply(1).toString());
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

    // Format the input string to the attribute style
    private String formatFilter(String s) {
        if(s.startsWith("cast")) {
            int p = s.indexOf("(");
            int q = s.indexOf(" ");
            s = s.substring(p+1, q);
        }
        return s;
    }

    // Get the name of the table using its attributes
    private String getTableName(List<String> attributes, List<Dataset<Row>> tables, List<String> tableNames) {
        for(int i = 0; i < tables.size(); i++) {
            String[] columns = tables.get(i).columns();
            if(this.isSameTable(attributes, columns)) {
                return tableNames.get(i);
            }
        }
        return null;
    }

    // Check if two tables have the same attributes
    private boolean isSameTable(List<String> attributes, String[] columns) {
        for(int x = 0; x < columns.length; x++) {
            if(columns[x].equals(this.cleanAttribute(attributes.get(x)))) {
                x++;
            } else {
                return false;
            }
        }
        return true;
    }

    // Recursively generate all the nodes in the relationTree
    private void generateChildren(LogicalPlan plan, BinaryNode<Relation> father) {
        if(plan.children().size() == 1) {
            Relation r = this.createRelation(plan.children().toList().apply(0));
            BinaryNode n = new BinaryNode(r);
            father.setLeft(n);
            n.setFather(father);
            this.generateChildren(plan.children().toList().apply(0), n);
        }
        else if(plan.children().size() == 2) {
            Relation r1 = this.createRelation(plan.children().toList().apply(0));
            BinaryNode n1 = new BinaryNode(r1);
            father.setLeft(n1);
            n1.setFather(father);
            this.generateChildren(plan.children().toList().apply(0), n1);
            Relation r2 = this.createRelation(plan.children().toList().apply(1));
            BinaryNode n2 = new BinaryNode(r2);
            father.setRight(n2);
            n2.setFather(father);
            this.generateChildren(plan.children().toList().apply(1), n2);
        }
    }

    // Generate the profile for each node with a post order visit
    private void generateProfiles(BinaryNode<Relation> node) {
        if (node == null) return;
        generateProfiles(node.getLeft());
        generateProfiles(node.getRight());
        this.setProfile(node);
    }

    // Set the profile of the current node considering the type of operation and its children
    private void setProfile(BinaryNode<Relation> node) {
        // If the node has no children is a leaf
        if(node.getLeft() == null && node.getRight() == null) {
            node.getElement().setProfile(this.buildLeafProfile(node));
        } else {
            node.getElement().setProfile(this.buildOperationProfile(node));
        }
    }

    // Generate the relation profile of a leaf
    private RelationProfile buildLeafProfile(BinaryNode<Relation> node) {
        RelationProfile profile = new RelationProfile();
        // The leaf represent the tables
        if(node.getElement().getOperation().equals("LogicalRelation")) {
            // All the attributes are set as visible plaintext
            profile.setVisiblePlaintext(node.getElement().getAttributes());
        } else {
            System.out.println("Error, not a table");
        }
        return profile;
    }

    // Generate the specific profile for each operation
    private RelationProfile buildOperationProfile(BinaryNode<Relation> node) {

        RelationProfile p = new RelationProfile();

        switch (node.getElement().getOperation()) {
            // All attributes became implicit except the ones of aggregate that remain visible
            case "Aggregate":
                // Check if the attributes of the project are plaintext or encrypted for the child and put them in the correct list
                for(int i=0; i < node.getElement().getAttributes().size(); i++) {
                    if (node.getLeft().getElement().getProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(i))) {
                        List<String> newVisiblePlaintext = new ArrayList<>();
                        newVisiblePlaintext.addAll(p.getVisiblePlaintext());
                        newVisiblePlaintext.add(node.getElement().getAttributes().get(i));
                        p.setVisiblePlaintext(newVisiblePlaintext);
                    }
                    else if (node.getLeft().getElement().getProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(i))) {
                        List<String> newVisibleEncrypted = new ArrayList<>();
                        newVisibleEncrypted.addAll(p.getVisibleEncrypted());
                        newVisibleEncrypted.add(node.getElement().getAttributes().get(i));
                        p.setVisibleEncrypted(newVisibleEncrypted);
                    }
                }
                p.setImplicitPlaintext(node.getLeft().getElement().getProfile().getImplicitPlaintext());
                for(int i = 0; i < node.getLeft().getElement().getProfile().getVisiblePlaintext().size(); i++) {
                    if(!p.getVisiblePlaintext().contains(node.getLeft().getElement().getProfile().getVisiblePlaintext().get(i))) {
                        List<String> newImplicitPlaintext = new ArrayList<>();
                        newImplicitPlaintext.addAll(p.getImplicitPlaintext());
                        newImplicitPlaintext.add(node.getLeft().getElement().getProfile().getVisiblePlaintext().get(i));
                        p.setImplicitPlaintext(newImplicitPlaintext);
                    }
                }
                p.setImplicitEncrypted(node.getLeft().getElement().getProfile().getImplicitEncrypted());
                for(int i = 0; i < node.getLeft().getElement().getProfile().getVisibleEncrypted().size(); i++) {
                    if(!p.getVisibleEncrypted().contains(node.getLeft().getElement().getProfile().getVisibleEncrypted().get(i))) {
                        List<String> newImplicitEncrypted = new ArrayList<>();
                        newImplicitEncrypted.addAll(p.getImplicitEncrypted());
                        newImplicitEncrypted.add(node.getLeft().getElement().getProfile().getVisibleEncrypted().get(i));
                        p.setImplicitEncrypted(newImplicitEncrypted);
                    }
                }
                p.setEquivalenceSets(node.getLeft().getElement().getProfile().getEquivalenceSets());
                break;
            // Select only the attributes of project as visible, the others remain the same as the child
            case "Project":
                // Check if the attributes of the project are plaintext or encrypted for the child and put them in the correct list
                for(int i=0; i < node.getElement().getAttributes().size(); i++) {
                    if (node.getLeft().getElement().getProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(i))) {
                        List<String> newVisiblePlaintext = new ArrayList<>();
                        newVisiblePlaintext.addAll(p.getVisiblePlaintext());
                        newVisiblePlaintext.add(node.getElement().getAttributes().get(i));
                        p.setImplicitEncrypted(newVisiblePlaintext);
                    }
                    else if (node.getLeft().getElement().getProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(i))) {
                        List<String> newVisibleEncrypted = new ArrayList<>();
                        newVisibleEncrypted.addAll(p.getVisibleEncrypted());
                        newVisibleEncrypted.add(node.getElement().getAttributes().get(i));
                        p.setVisibleEncrypted(newVisibleEncrypted);
                    }
                }
                p.setImplicitPlaintext(node.getLeft().getElement().getProfile().getImplicitPlaintext());
                p.setImplicitEncrypted(node.getLeft().getElement().getProfile().getImplicitEncrypted());
                p.setEquivalenceSets(node.getLeft().getElement().getProfile().getEquivalenceSets());
                break;
            case "Filter":
                p.setVisiblePlaintext(node.getLeft().getElement().getProfile().getVisiblePlaintext());
                p.setVisibleEncrypted(node.getLeft().getElement().getProfile().getVisibleEncrypted());
                p.setImplicitPlaintext(node.getLeft().getElement().getProfile().getImplicitPlaintext());
                p.setImplicitEncrypted(node.getLeft().getElement().getProfile().getImplicitEncrypted());
                // If one of the two attributes is numeric add the attribute not numeric to the implicit plaintext
                if(node.getElement().getAttributes().size() == 2 && (Character.isDigit(node.getElement().getAttributes().get(0).charAt(0)) ^ Character.isDigit(node.getElement().getAttributes().get(1).charAt(0)))) {
                    if(!Character.isDigit(node.getElement().getAttributes().get(0).charAt(0))) {
                        List<String> newImplicitPlaintext = new ArrayList<>();
                        newImplicitPlaintext.addAll(p.getImplicitPlaintext());
                        newImplicitPlaintext.add(node.getElement().getAttributes().get(0));
                        p.setImplicitPlaintext(newImplicitPlaintext);
                    }
                    else {
                        List<String> newImplicitPlaintext = new ArrayList<>();
                        newImplicitPlaintext.addAll(p.getImplicitPlaintext());
                        newImplicitPlaintext.add(node.getElement().getAttributes().get(1));
                        p.setImplicitPlaintext(newImplicitPlaintext);
                    }
                } // If both attributes are not numeric add them to the implicit list
                else if(node.getElement().getAttributes().size() == 2 && (!Character.isDigit(node.getElement().getAttributes().get(0).charAt(0)) && !Character.isDigit(node.getElement().getAttributes().get(1).charAt(0)))) {
                    // If both are plaintext add them to implicit plaintext
                    if (node.getLeft().getElement().getProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(0)) && node.getLeft().getElement().getProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(1))) {
                        List<String> newVisiblePlaintext = new ArrayList<>();
                        newVisiblePlaintext.addAll(p.getVisiblePlaintext());
                        newVisiblePlaintext.addAll(node.getElement().getAttributes());
                        p.setVisiblePlaintext(newVisiblePlaintext);
                    } // If both are encrypted add them to implicit encrypted
                    else if (node.getLeft().getElement().getProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(0)) && node.getLeft().getElement().getProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(1))) {
                        List<String> newVisibleEncrypted = new ArrayList<>();
                        newVisibleEncrypted.addAll(p.getVisibleEncrypted());
                        newVisibleEncrypted.addAll(node.getElement().getAttributes());
                        p.setVisibleEncrypted(newVisibleEncrypted);
                    }
                    else System.out.println("Error: Attributes with different visibility");
                }
                p.setEquivalenceSets(node.getLeft().getElement().getProfile().getEquivalenceSets());
                // If both attributes are not numeric values add the equivalence set of the two attributes
                if(node.getElement().getAttributes().size() == 2 && (!Character.isDigit(node.getElement().getAttributes().get(0).charAt(0)) && !Character.isDigit(node.getElement().getAttributes().get(1).charAt(0)))) {
                    List<List<String>> eSet = new ArrayList<>();
                    List<String> attributes = new ArrayList<>();
                    attributes.addAll(node.getElement().getAttributes());
                    eSet.addAll(p.getEquivalenceSets());
                    eSet.add(attributes);
                    p.setEquivalenceSets(eSet);
                }
                break;
            // Join visible and implicit attributes of the children and add an equivalence for the two attributes in the join
            case "Join":
                List<String> l = joinLists(node.getLeft().getElement().getProfile().getVisiblePlaintext(), node.getRight().getElement().getProfile().getVisiblePlaintext());
                p.setVisiblePlaintext(l);
                l = joinLists(node.getLeft().getElement().getProfile().getVisibleEncrypted(), node.getRight().getElement().getProfile().getVisibleEncrypted());
                p.setVisibleEncrypted(l);
                l = joinLists(node.getLeft().getElement().getProfile().getImplicitPlaintext(), node.getRight().getElement().getProfile().getImplicitPlaintext());
                p.setImplicitPlaintext(l);
                l = joinLists(node.getLeft().getElement().getProfile().getImplicitEncrypted(), node.getRight().getElement().getProfile().getImplicitEncrypted());
                p.setImplicitEncrypted(l);
                // The attributes of the join condition have to be added to the implicit list
                if(node.getElement().getAttributes().size() == 2) {
                    // If both are plaintext add them to implicit plaintext
                    if (node.getLeft().getElement().getProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(0)) && node.getLeft().getElement().getProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(1))) {
                        List<String> newImplicitPlaintext = new ArrayList<>();
                        newImplicitPlaintext.addAll(p.getImplicitPlaintext());
                        newImplicitPlaintext.addAll(node.getElement().getAttributes());
                        p.setImplicitPlaintext(newImplicitPlaintext);
                    } // If both are encrypted add them to implicit encrypted
                    else if (node.getLeft().getElement().getProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(0)) && node.getLeft().getElement().getProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(1))) {
                        List<String> newImplicitEncrypted = new ArrayList<>();
                        newImplicitEncrypted.addAll(p.getImplicitEncrypted());
                        newImplicitEncrypted.addAll(node.getElement().getAttributes());
                        p.setImplicitEncrypted(newImplicitEncrypted);
                    }
                    else System.out.println("Error: Attributes with different visibility");
                }
                List<List<String>> ll = joinListsLists(node.getLeft().getElement().getProfile().getEquivalenceSets(), node.getRight().getElement().getProfile().getEquivalenceSets());
                p.setEquivalenceSets(ll);
                l = node.getElement().getAttributes();
                p.getEquivalenceSets().add(l);
                break;
            default:
                System.out.println("Error: unknown operation or incorrect tree");
        }
        return p;
    }

    // Support method to join two lists without duplicates
    private List<String> joinLists(List<String> l1, List<String> l2) {

        List<String> list = new ArrayList<>();

        if(l1 != null) {
            list.addAll(l1);
            if (l2 != null) {
                for (int i = 0; i < l2.size(); i++) {
                    if (!list.contains(l2.get(i))) {
                        list.add(l2.get(i));
                    }
                }
            }
        } else if(l2 != null) {
            list.addAll(l2);
        }
        return list;
    }

    // Support method to join two lists of lists without duplicates
    private List<List<String>> joinListsLists(List<List<String>> l1, List<List<String>> l2) {

        List<List<String>> list = new ArrayList<>();

        if(l1 != null) {
            list.addAll(l1);
            if (l2 != null) {
                for (int i = 0; i < l2.size(); i++) {
                    if (!list.contains(l2.get(i))) {
                        list.add(l2.get(i));
                    }
                }
            }
        } else if(l2 != null) {
            list.addAll(l2);
        }
        return list;
    }

    // Cut the index from the attribute name
    public static String cleanAttribute(String s) {
        CharSequence c = s.subSequence(0, s.indexOf("#"));
        String r = c.toString();
        return r;
    }

    // Eliminate duplicates from a list
    public static void eliminateDuplicate(List<String> l) {
        // Add elements to al, including duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(l);
        l.clear();
        l.addAll(hs);
    }
}
