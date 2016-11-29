package RelationProfileTreeBuilder;

import CostModel.SubplansMap;

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
    // Relation relationProfile of the relation
    private RelationProfile relationProfile;
    // Name of the table (only for LogicalRelations)
    private String tableName;
    // Map of subplan costs
    private SubplansMap subplansMap;

    public Relation(String o, List<String> a, long s) {
        this.operation = o;
        this.attributes = a;
        this.syzeInBytes = s;
        this.relationProfile = null;
        this.tableName = "Not a table";
        this.subplansMap = new SubplansMap();
    }

    public Relation(String o, List<String> a, RelationProfile p, long s) {
        this.operation = o;
        this.attributes = a;
        this.syzeInBytes = s;
        this.relationProfile = p;
        this.tableName = "Not a table";
        this.subplansMap = new SubplansMap();
    }

    public Relation(String o, List<String> a, RelationProfile p, String t, long s) {
        this.operation = o;
        this.attributes = a;
        this.syzeInBytes = s;
        this.relationProfile = p;
        this.tableName = t;
        this.subplansMap = new SubplansMap();
    }

    public Relation(String o, List<String> a, String t, long s) {
        this.operation = o;
        this.attributes = a;
        this.syzeInBytes = s;
        this.relationProfile = null;
        this.tableName = t;
        this.subplansMap = new SubplansMap();
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

    public SubplansMap getSubplansMap() {
        return subplansMap;
    }

    public void setSubplansMap(SubplansMap subplansMap) {
        this.subplansMap = subplansMap;
    }

    public String toString() {
        return "Operation: " + this.operation + "\n" +
                "Attributes: " + this.attributes.toString() + "\n" +
                "Syze in Byte: " + this.syzeInBytes + "\n" +
                "Table Name: " + this.tableName + "\n" +
                this.relationProfile.toString() + "end relation" +"\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Relation)) return false;

        Relation relation = (Relation) o;

        if (syzeInBytes != relation.syzeInBytes) return false;
        if (operation != null ? !operation.equals(relation.operation) : relation.operation != null) return false;
        if (attributes != null ? !attributes.equals(relation.attributes) : relation.attributes != null) return false;
        return tableName != null ? tableName.equals(relation.tableName) : relation.tableName == null;

    }

    @Override
    public int hashCode() {
        int result = operation != null ? operation.hashCode() : 0;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (int) (syzeInBytes ^ (syzeInBytes >>> 32));
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        return result;
    }
}
