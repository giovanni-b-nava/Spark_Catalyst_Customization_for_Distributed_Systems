package RelationTreeBuilder;

import java.util.List;

/**
 * Created by Spark on 17/11/2016.
 */
public class Relation {

    // Type of operation
    private String operation;
    // Attributes involved in the operation
    private List<String> attributes;

    public Relation(String o, List<String> a) {
        this.operation = o;
        this.attributes = a;
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

    public String toString() {
        return "Operation: " + this.operation + "; Attributes: " + this.attributes.toString() + "\n";
    }
}
