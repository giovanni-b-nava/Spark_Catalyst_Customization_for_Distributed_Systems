package DataStructureBuilder;

import java.util.List;

/**
 * Created by Spark on 17/11/2016.
 */
public class Relation {

    private String operator;
    private List<String> attributes;

    public Relation(String o, List<String> a) {
        this.operator = o;
        this.attributes = a;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public String toString() {
        return "Operation: " + this.operator + "; Attributes: " + this.attributes.toString() + "\n";
    }
}
