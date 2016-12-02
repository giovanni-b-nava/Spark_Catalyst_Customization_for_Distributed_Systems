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
    private long sizeInBytes;
    // Relation relationProfile of the relation
    private RelationProfile relationProfile;
    // Name of the table (only for LogicalRelations)
    private String tableName;

    public Relation(String o, List<String> a, long s) {
        this.operation = o;
        this.attributes = a;
        this.sizeInBytes = s;
        this.relationProfile = null;
        this.tableName = "Not a table";
    }

    public Relation(String o, List<String> a, RelationProfile p, long s) {
        this.operation = o;
        this.attributes = a;
        this.sizeInBytes = s;
        this.relationProfile = p;
        this.tableName = "Not a table";
    }

    public Relation(String o, List<String> a, RelationProfile p, String t, long s) {
        this.operation = o;
        this.attributes = a;
        this.sizeInBytes = s;
        this.relationProfile = p;
        this.tableName = t;
    }

    public Relation(String o, List<String> a, String t, long s) {
        this.operation = o;
        this.attributes = a;
        this.sizeInBytes = s;
        this.relationProfile = null;
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

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public RelationProfile getRelationProfile() {
        return relationProfile;
    }

    public void setRelationProfile(RelationProfile relationProfile) {
        this.relationProfile = relationProfile;
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
                "Size in Byte: " + this.sizeInBytes + "\n" +
                "Table Name: " + this.tableName + "\n" +
                this.relationProfile.toString() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relation)) return false;

        Relation relation = (Relation) o;

        if (sizeInBytes != relation.sizeInBytes) return false;
        if (operation != null ? !operation.equals(relation.operation) : relation.operation != null) return false;
        if (attributes != null ? !attributes.equals(relation.attributes) : relation.attributes != null) return false;
        return tableName != null ? tableName.equals(relation.tableName) : relation.tableName == null;

    }

    @Override
    public int hashCode() {
        int result = operation != null ? operation.hashCode() : 0;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (int) (sizeInBytes ^ (sizeInBytes >>> 32));
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }
}
