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

public class RelationProfileTree
{

    // Tree representing the relations in the optimized plan
    private BinaryTree<Relation> relationTree;

    // Builds the relationTree with the relations representing the operations
    public RelationProfileTree(LogicalPlan plan)
    {
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
    private Relation createRelation(LogicalPlan plan)
    {
        String operation = plan.nodeName();
        long sizeInByte = plan.statistics().sizeInBytes().longValue();
        // Table
        if(operation.equals("LogicalRelation"))
        {
            List<String> attributes = this.collectAttributes(plan);
            String tableName = this.getTableName(attributes, DataBuilder.getDataBuilder().tables, DataBuilder.getDataBuilder().tableNames);
            return new Relation(operation, attributes, tableName, sizeInByte);
        }
        else // Operation
        {
            List<String> e = this.collectAttributes(plan);
            String filterOperator = this.collectFilterOperator(plan);
            return new Relation(operation, e, sizeInByte, filterOperator);
        }
    }

    // Generates the list of attributes for the current operation
    private List<String> collectAttributes(LogicalPlan plan)
    {

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
                while(y < plan.apply(0).constraints().toList().size())
                {
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
                System.out.println("collectAttributes.Default");
        }
        return l;
    }

    private String collectFilterOperator(LogicalPlan plan)
    {
        String operator = "";
        switch (plan.nodeName())
        {
            case "Filter":
                int x=0;
                while(x < plan.apply(0).constraints().toList().size())
                {
                    if(plan.apply(0).constraints().toList().apply(x).flatArguments().toList().size() == 2)
                    {
                        operator = plan.apply(0).constraints().toList().apply(1).prettyName();
                    }
                    x++;
                }
                break;
        }

        return operator;
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
    private String getTableName(List<String> attributes, List<Dataset<Row>> tables, List<String> tableNames)
    {
        for(int i = 0; i < tables.size(); i++) {
            String[] columns = tables.get(i).columns();
            if(this.isSameTable(attributes, columns)) {
                return tableNames.get(i);
            }
        }
        return null;
    }

    // Check if two tables have the same attributes
    private boolean isSameTable(List<String> attributes, String[] columns)
    {
        for(int x = 0; x < columns.length; x++) {
            if(columns[x].equals(this.cleanAttribute(attributes.get(x)))) {
                x++;
            } else {
                return false;
            }
        }
        return true;
    }

    // Recursively generate all the providers in the relationTree
    private void generateChildren(LogicalPlan plan, BinaryNode<Relation> father)
    {
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
    private void generateProfiles(BinaryNode<Relation> node)
    {
        if (node == null) return;
        generateProfiles(node.getLeft());
        generateProfiles(node.getRight());
        this.setProfile(node);
    }

    // Set the profile of the current node considering the type of operation and its children
    public void setProfile(BinaryNode<Relation> node)
    {
        // Node without children = leaf
        if(node.getLeft() == null && node.getRight() == null)
        {
            // The RelationProfile of a LogicalRelation is now evaluated
            // in CostModel.generatePlans (BASE CASE)
            node.getElement().setRelationProfile(new RelationProfile());

            // Integrity Check: Is a table?
            if(! node.getElement().getOperation().equals("LogicalRelation"))
                System.out.println("RelationProfileTree.setProfile: ERROR, not a table!");
        }
        else
        {
            node.getElement().setRelationProfile(this.buildOperationProfile(node));
        }
    }

    // Generate the specific profile for each operation
    public RelationProfile buildOperationProfile(BinaryNode<Relation> node)
    {

        RelationProfile p = new RelationProfile();

        switch (node.getElement().getOperation())
        {
            // All attributes became implicit except the ones of aggregate that remain visible
            case "Aggregate":
                // Check if the attributes of the aggregate are plaintext or encrypted for the child and put them in the correct list
                for(int i=0; i < node.getElement().getAttributes().size(); i++)
                {
                    if (node.getLeft().getElement().getRelationProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(i))) {
                        List<String> newVisiblePlaintext = new ArrayList<>();
                        newVisiblePlaintext.addAll(p.getVisiblePlaintext());
                        newVisiblePlaintext.add(node.getElement().getAttributes().get(i));
                        eliminateDuplicate(newVisiblePlaintext);
                        p.setVisiblePlaintext(newVisiblePlaintext);
                    }
                    else if (node.getLeft().getElement().getRelationProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(i))) {
                        List<String> newVisibleEncrypted = new ArrayList<>();
                        newVisibleEncrypted.addAll(p.getVisibleEncrypted());
                        newVisibleEncrypted.add(node.getElement().getAttributes().get(i));
                        eliminateDuplicate(newVisibleEncrypted);
                        p.setVisibleEncrypted(newVisibleEncrypted);
                    }
                }
                p.setImplicitPlaintext(node.getLeft().getElement().getRelationProfile().getImplicitPlaintext());
                for(int i = 0; i < node.getLeft().getElement().getRelationProfile().getVisiblePlaintext().size(); i++) {
                    if(!p.getVisiblePlaintext().contains(node.getLeft().getElement().getRelationProfile().getVisiblePlaintext().get(i))) {
                        List<String> newImplicitPlaintext = new ArrayList<>();
                        newImplicitPlaintext.addAll(p.getImplicitPlaintext());
                        newImplicitPlaintext.add(node.getLeft().getElement().getRelationProfile().getVisiblePlaintext().get(i));
                        eliminateDuplicate(newImplicitPlaintext);
                        p.setImplicitPlaintext(newImplicitPlaintext);
                    }
                }
                p.setImplicitEncrypted(node.getLeft().getElement().getRelationProfile().getImplicitEncrypted());
                for(int i = 0; i < node.getLeft().getElement().getRelationProfile().getVisibleEncrypted().size(); i++) {
                    if(!p.getVisibleEncrypted().contains(node.getLeft().getElement().getRelationProfile().getVisibleEncrypted().get(i))) {
                        List<String> newImplicitEncrypted = new ArrayList<>();
                        newImplicitEncrypted.addAll(p.getImplicitEncrypted());
                        newImplicitEncrypted.add(node.getLeft().getElement().getRelationProfile().getVisibleEncrypted().get(i));
                        eliminateDuplicate(newImplicitEncrypted);
                        p.setImplicitEncrypted(newImplicitEncrypted);
                    }
                }
                p.setEquivalenceSets(node.getLeft().getElement().getRelationProfile().getEquivalenceSets());
                break;
            // Select only the attributes of project as visible, the others remain the same as the child
            case "Project":
                // Check if the attributes of the project are plaintext or encrypted for the child and put them in the correct list
                for(int i=0; i < node.getElement().getAttributes().size(); i++) {
                    if (node.getLeft().getElement().getRelationProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(i))) {
                        List<String> newVisiblePlaintext = new ArrayList<>();
                        newVisiblePlaintext.addAll(p.getVisiblePlaintext());
                        newVisiblePlaintext.add(node.getElement().getAttributes().get(i));
                        eliminateDuplicate(newVisiblePlaintext);
                        p.setVisiblePlaintext(newVisiblePlaintext);
                    }
                    else if (node.getLeft().getElement().getRelationProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(i))) {
                        List<String> newVisibleEncrypted = new ArrayList<>();
                        newVisibleEncrypted.addAll(p.getVisibleEncrypted());
                        newVisibleEncrypted.add(node.getElement().getAttributes().get(i));
                        eliminateDuplicate(newVisibleEncrypted);
                        p.setVisibleEncrypted(newVisibleEncrypted);
                    }
                }
                p.setImplicitPlaintext(node.getLeft().getElement().getRelationProfile().getImplicitPlaintext());
                p.setImplicitEncrypted(node.getLeft().getElement().getRelationProfile().getImplicitEncrypted());
                p.setEquivalenceSets(node.getLeft().getElement().getRelationProfile().getEquivalenceSets());
                break;
            case "Filter":
                p.setVisiblePlaintext(node.getLeft().getElement().getRelationProfile().getVisiblePlaintext());
                p.setVisibleEncrypted(node.getLeft().getElement().getRelationProfile().getVisibleEncrypted());
                p.setImplicitPlaintext(node.getLeft().getElement().getRelationProfile().getImplicitPlaintext());
                p.setImplicitEncrypted(node.getLeft().getElement().getRelationProfile().getImplicitEncrypted());
                // If one of the two attributes is numeric add the attribute not numeric to the implicit plaintext
                if(node.getElement().getAttributes().size() == 2 && (Character.isDigit(node.getElement().getAttributes().get(0).charAt(0)) ^ Character.isDigit(node.getElement().getAttributes().get(1).charAt(0)))) {
                    if(!Character.isDigit(node.getElement().getAttributes().get(0).charAt(0))) {
                        List<String> newImplicitPlaintext = new ArrayList<>();
                        newImplicitPlaintext.addAll(p.getImplicitPlaintext());
                        newImplicitPlaintext.add(node.getElement().getAttributes().get(0));
                        eliminateDuplicate(newImplicitPlaintext);
                        p.setImplicitPlaintext(newImplicitPlaintext);
                    }
                    else {
                        List<String> newImplicitPlaintext = new ArrayList<>();
                        newImplicitPlaintext.addAll(p.getImplicitPlaintext());
                        newImplicitPlaintext.add(node.getElement().getAttributes().get(1));
                        eliminateDuplicate(newImplicitPlaintext);
                        p.setImplicitPlaintext(newImplicitPlaintext);
                    }
                } // If both attributes are not numeric add them to the implicit list
                else if(node.getElement().getAttributes().size() == 2 && (!Character.isDigit(node.getElement().getAttributes().get(0).charAt(0)) && !Character.isDigit(node.getElement().getAttributes().get(1).charAt(0)))) {
                    // If both are plaintext add them to implicit plaintext
                    if (node.getLeft().getElement().getRelationProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(0)) && node.getLeft().getElement().getRelationProfile().getVisiblePlaintext().contains(node.getElement().getAttributes().get(1))) {
                        List<String> newImplicitPlaintext = new ArrayList<>();
                        newImplicitPlaintext.addAll(p.getImplicitPlaintext());
                        newImplicitPlaintext.addAll(node.getElement().getAttributes());
                        eliminateDuplicate(newImplicitPlaintext);
                        p.setImplicitPlaintext(newImplicitPlaintext);
                    } // If both are encrypted add them to implicit encrypted
                    else if (node.getLeft().getElement().getRelationProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(0)) && node.getLeft().getElement().getRelationProfile().getVisibleEncrypted().contains(node.getElement().getAttributes().get(1))) {
                        List<String> newImplicitEncrypted = new ArrayList<>();
                        newImplicitEncrypted.addAll(p.getImplicitEncrypted());
                        newImplicitEncrypted.addAll(node.getElement().getAttributes());
                        eliminateDuplicate(newImplicitEncrypted);
                        p.setImplicitEncrypted(newImplicitEncrypted);
                    }
                    else System.out.println("ERROR: Filter with different visibility");
                }
                p.setEquivalenceSets(node.getLeft().getElement().getRelationProfile().getEquivalenceSets());
                // If both attributes are not numeric values add the equivalence set of the two attributes
                if(node.getElement().getAttributes().size() == 2 && (!Character.isDigit(node.getElement().getAttributes().get(0).charAt(0)) && !Character.isDigit(node.getElement().getAttributes().get(1).charAt(0)))) {
                    List<List<String>> eSet = new ArrayList<>();
                    List<String> attributes = new ArrayList<>();
                    attributes.addAll(node.getElement().getAttributes());
                    eSet.addAll(p.getEquivalenceSets());
                    eSet.add(attributes);
                    eliminateDuplicateLists(eSet);
                    p.setEquivalenceSets(eSet);
                }
                break;
            // Join visible and implicit attributes of the children and add an equivalence for the two attributes in the join
            case "Join":
                List<String> l = joinLists(node.getLeft().getElement().getRelationProfile().getVisiblePlaintext(), node.getRight().getElement().getRelationProfile().getVisiblePlaintext());
                p.setVisiblePlaintext(l);
                l = joinLists(node.getLeft().getElement().getRelationProfile().getVisibleEncrypted(), node.getRight().getElement().getRelationProfile().getVisibleEncrypted());
                p.setVisibleEncrypted(l);
                l = joinLists(node.getLeft().getElement().getRelationProfile().getImplicitPlaintext(), node.getRight().getElement().getRelationProfile().getImplicitPlaintext());
                p.setImplicitPlaintext(l);
                l = joinLists(node.getLeft().getElement().getRelationProfile().getImplicitEncrypted(), node.getRight().getElement().getRelationProfile().getImplicitEncrypted());
                p.setImplicitEncrypted(l);
                // The attributes of the join condition have to be added to the implicit list
                if(node.getElement().getAttributes().size() == 2)
                {
                    // If they have different visibility add them to implicit encrypted (priority to security)
                    if (
                            (p.getVisiblePlaintext().contains(node.getElement().getAttributes().get(0)) && !p.getVisiblePlaintext().contains(node.getElement().getAttributes().get(1)))
                                    ||
                                    (!p.getVisiblePlaintext().contains(node.getElement().getAttributes().get(0)) && p.getVisiblePlaintext().contains(node.getElement().getAttributes().get(1)))
                            )
                    {
                        if (p.getVisiblePlaintext().contains(node.getElement().getAttributes().get(0)))
                        {
                            p.getVisibleEncrypted().add(node.getElement().getAttributes().get(0));
                            p.getVisiblePlaintext().remove(node.getElement().getAttributes().get(0));
                        }
                        else
                        {
                            p.getVisibleEncrypted().add(node.getElement().getAttributes().get(1));
                            p.getVisiblePlaintext().remove(node.getElement().getAttributes().get(1));
                        }
                    }

                    // If both are plaintext add them to implicit plaintext
                    if (p.getVisiblePlaintext().contains(node.getElement().getAttributes().get(0)) && p.getVisiblePlaintext().contains(node.getElement().getAttributes().get(1)))
                    {
                        List<String> newImplicitPlaintext = new ArrayList<>();
                        newImplicitPlaintext.addAll(p.getImplicitPlaintext());
                        newImplicitPlaintext.addAll(node.getElement().getAttributes());
                        eliminateDuplicate(newImplicitPlaintext);
                        p.setImplicitPlaintext(newImplicitPlaintext);
                    } // If both are encrypted add them to implicit encrypted
                    else if (p.getVisibleEncrypted().contains(node.getElement().getAttributes().get(0)) && p.getVisibleEncrypted().contains(node.getElement().getAttributes().get(1)))
                    {
                        List<String> newImplicitEncrypted = new ArrayList<>();
                        newImplicitEncrypted.addAll(p.getImplicitEncrypted());
                        newImplicitEncrypted.addAll(node.getElement().getAttributes());
                        eliminateDuplicate(newImplicitEncrypted);
                        p.setImplicitEncrypted(newImplicitEncrypted);
                    }
                    // TODO Verificare...
//                    else
//                    {
//                        System.out.println("ERROR: Join with different visibility");
//                    }
                }
                List<List<String>> ll = joinListsLists(node.getLeft().getElement().getRelationProfile().getEquivalenceSets(), node.getRight().getElement().getRelationProfile().getEquivalenceSets());
                p.setEquivalenceSets(ll);
                l = node.getElement().getAttributes();
                List<List<String>> eSet = new ArrayList<>();
                eSet.addAll(p.getEquivalenceSets());
                eSet.add(l);
                eliminateDuplicateLists(eSet);
                p.setEquivalenceSets(eSet);
                break;
            default:
                System.out.println("Error: unknown operation or incorrect tree");
        }
        return p;
    }

    // Support method to join two lists without duplicates
    private List<String> joinLists(List<String> l1, List<String> l2)
    {

        List<String> list = new ArrayList<>();

        if(l1 != null)
        {
            list.addAll(l1);
            if (l2 != null)
            {
                for (int i = 0; i < l2.size(); i++)
                {
                    if (!list.contains(l2.get(i)))
                    {
                        list.add(l2.get(i));
                    }
                }
            }
        } else if(l2 != null)
        {
            list.addAll(l2);
        }
        return list;
    }

    // Support method to join two lists of lists without duplicates
    private List<List<String>> joinListsLists(List<List<String>> l1, List<List<String>> l2)
    {

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
    public static String cleanAttribute(String s)
    {
        CharSequence c = s.subSequence(0, s.indexOf("#"));
        String r = c.toString();
        return r;
    }

    // Eliminate duplicates from a list
    public static void eliminateDuplicate(List<String> l)
    {
        // Add elements to al, including duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(l);
        l.clear();
        l.addAll(hs);
    }

    // Eliminate duplicates from a list of lists
    public static void eliminateDuplicateLists(List<List<String>> l)
    {
        // Add elements to al, including duplicates
        Set<List<String>> hs = new HashSet<>();
        hs.addAll(l);
        l.clear();
        l.addAll(hs);
    }

}
