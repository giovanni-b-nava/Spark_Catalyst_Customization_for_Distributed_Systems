package RelationProfileTreeBuilder;

import java.util.List;

/**
 * Created by Spark on 17/11/2016.
 */
public class Relation {

    // Type of operation
    private String operation;
    // Attributes involved in the operation
    private List<String> attributes;
    // Relation profile of the relation
    private RelationProfile profile;
    // Name of the table (only for LogicalRelations)
    private String tableName;

    public Relation(String o, List<String> a) {
        this.operation = o;
        this.attributes = a;
        this.profile = null;
        this.tableName = null;
    }

    public Relation(String o, List<String> a, RelationProfile p) {
        this.operation = o;
        this.attributes = a;
        this.profile = p;
        this.tableName = null;
    }

    public Relation(String o, List<String> a, RelationProfile p, String t) {
        this.operation = o;
        this.attributes = a;
        this.profile = p;
        this.tableName = t;
    }

    public Relation(String o, List<String> a, String t) {
        this.operation = o;
        this.attributes = a;
        this.profile = null;
        this.tableName = t;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public RelationProfile getProfile() {
        return profile;
    }

    public void setProfile(RelationProfile profile) {
        this.profile = profile;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String toString() {
        return "Operation: " + this.operation + "\n" +
                "Attributes: " + this.attributes.toString() + "\n"; /*+
                this.profile.toString() + "\n" + "end relation";*/
    }
}
