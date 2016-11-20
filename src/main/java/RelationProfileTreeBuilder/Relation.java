package RelationProfileTreeBuilder;

import RelationProfileTreeBuilder.RelationProfile;

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

    public Relation(String o, List<String> a) {
        this.operation = o;
        this.attributes = a;
        this.profile = null;
    }

    public Relation(String o, List<String> a, RelationProfile p) {
        this.operation = o;
        this.attributes = a;
        this.profile = p;
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

    public String toString() {
        return "Operation: " + this.operation + "; Attributes: " + this.attributes.toString() + "\n";
    }
}
