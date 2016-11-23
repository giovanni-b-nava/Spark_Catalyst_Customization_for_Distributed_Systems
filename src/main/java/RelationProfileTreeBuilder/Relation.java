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
    // Size in byte of the data in the operation
    private long syzeInBytes;
    // Relation profile of the relation
    private RelationProfile profile;
    // Name of the table (only for LogicalRelations)
    private String tableName;

    public Relation(String o, List<String> a, long s) {
        this.operation = o;
        this.attributes = a;
        this.syzeInBytes = s;
        this.profile = null;
        this.tableName = "Not a table";
    }

    public Relation(String o, List<String> a, RelationProfile p, long s) {
        this.operation = o;
        this.attributes = a;
        this.syzeInBytes = s;
        this.profile = p;
        this.tableName = "Not a table";
    }

    public Relation(String o, List<String> a, RelationProfile p, String t, long s) {
        this.operation = o;
        this.attributes = a;
        this.syzeInBytes = s;
        this.profile = p;
        this.tableName = t;
    }

    public Relation(String o, List<String> a, String t, long s) {
        this.operation = o;
        this.attributes = a;
        this.syzeInBytes = s;
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

    public long getSyzeInBytes() {
        return syzeInBytes;
    }

    public void setSyzeInBytes(long syzeInBytes) {
        this.syzeInBytes = syzeInBytes;
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
                "Attributes: " + this.attributes.toString() + "\n" +
                "Syze in Byte: " + this.syzeInBytes + "\n" +
                "Table Name: " + this.tableName + "\n" +
                this.profile.toString() + "end relation" +"\n";
    }
}
