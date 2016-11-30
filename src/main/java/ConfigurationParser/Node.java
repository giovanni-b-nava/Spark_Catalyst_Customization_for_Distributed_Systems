package ConfigurationParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spark on 09/11/2016.
 */
public class Node {

    // Name of the node
    private String name;
    // Type of the node
    private String category;
    // Costs attached to the node
    private Costs costs;
    // Connections between the providers (two ordered lists: one with the names of the providers and one with the throughput values)
    private Links links;
    // List of tables with plaintext and encrypted attributes
    private List<Table> tables = new ArrayList<>();

    public Node(String n, String c, Costs co, Links l, List<Table> t) {
        this.name = n;
        this.category = c;
        this.costs = co;
        this.links = l;
        this.tables = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Costs getCosts() {
        return costs;
    }

    public void setCosts(Costs costs) {
        this.costs = costs;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;

        Node node = (Node) o;

        if (name != null ? !name.equals(node.name) : node.name != null) return false;
        if (category != null ? !category.equals(node.category) : node.category != null) return false;
        return costs != null ? costs.equals(node.costs) : node.costs == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (costs != null ? costs.hashCode() : 0);
        return result;
    }
}
