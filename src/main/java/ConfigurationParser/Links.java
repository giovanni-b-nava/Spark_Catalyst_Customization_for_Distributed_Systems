package ConfigurationParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spark on 14/11/2016.
 */
public class Links {

    private List<String> name = new ArrayList<>();
    private List<Double> costPerGB = new ArrayList<>();

    public Links(List<String> n, List<Double> t) {
        this.name = n;
        this.costPerGB = t;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<Double> getCostPerGB() {
        return costPerGB;
    }

    public void setCostPerGB(List<Double> costPerGB) {
        this.costPerGB = costPerGB;
    }

}
