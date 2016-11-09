import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Spark on 09/11/2016.
 */
public class Node {

    private String name;
    private String category;
    private Map<String, Double> encryption = new HashMap<>();
    private Map<String, Double> costs = new HashMap<>();
    private Map<String, Double> links = new HashMap<>();
    private List<Table> tables = new ArrayList<>();

    public Node(String n, String c, Map<String, Double> e, Map<String, Double> co, Map<String, Double> l, List<Table> t) {
        this.name = n;
        this.category = c;
        this.encryption = e;
        this.costs = co;
        this.links = l;
        this.tables = t;
    }

    public Map<String, Double> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, Double> costs) {
        this.costs = costs;
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

    public Map<String, Double> getEncryption() {
        return encryption;
    }

    public void setEncryption(Map<String, Double> encryption) {
        this.encryption = encryption;
    }

    public Map<String, Double> getLinks() {
        return links;
    }

    public void setLinks(Map<String, Double> links) {
        this.links = links;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }
}
