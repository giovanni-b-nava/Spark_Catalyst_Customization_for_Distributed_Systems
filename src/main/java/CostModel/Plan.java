package CostModel;

import ConfigurationParser.Provider;
import RelationProfileTreeBuilder.Relation;
import TreeStructure.BinaryNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giovanni on 24/11/2016.
 */
public class Plan implements Comparable
{

    private BinaryNode<Relation> relation;
    private double cost;
    private List<Provider> assignedProviders;

    public Plan()
    {
        assignedProviders = new ArrayList<>();
    }

    public BinaryNode<Relation> getRelation()
    {
        return relation;
    }

    public void setRelation(BinaryNode<Relation> relation)
    {
        this.relation = relation;
    }

    public double getCost(){
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public List<Provider> getAssignedProviders() {
        return assignedProviders;
    }

    public void setAssignedProviders(List<Provider> assignedProviders) {
        this.assignedProviders = assignedProviders;
    }

    public void AddAssignedProviders(Provider provider)
    {
        assignedProviders.add(provider);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Plan)) return false;

        Plan plan = (Plan) o;

        if (Double.compare(plan.cost, cost) != 0) return false;
        if (relation != null ? !relation.equals(plan.relation) : plan.relation != null) return false;
        return assignedProviders != null ? assignedProviders.equals(plan.assignedProviders) : plan.assignedProviders == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = relation != null ? relation.hashCode() : 0;
        temp = Double.doubleToLongBits(cost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (assignedProviders != null ? assignedProviders.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Plan{\n" +
                "relation=" + relation.getElement().toString() +
                ", cost=" + cost +
                "\n, assignedProviders=\n" + assignedProviders.toString() +
                "\n}";
    }

    @Override
    public int compareTo(Object o)
    {
        if (o instanceof Plan)
        {
            if (this.getCost() > ((Plan) o).getCost())
                return 1;
            else
            {
                if (this.getCost() == ((Plan) o).getCost())
                    return 0;
                else
                    return -1;
            }
        }
        return 0;
    }
}
