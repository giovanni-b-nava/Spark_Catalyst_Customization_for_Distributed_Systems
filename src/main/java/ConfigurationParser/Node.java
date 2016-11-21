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
    // Connections between the nodes (two ordered lists: one with the names of the nodes and one with the throughput values)
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
}
