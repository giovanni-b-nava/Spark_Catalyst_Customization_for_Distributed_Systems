package CostModel;

import ConfigurationParser.Provider;
import RelationProfileTreeBuilder.Relation;
import TreeStructure.BinaryNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Giovanni on 24/11/2016.
 */
public class Plan
{

    private BinaryNode<Relation> relation;
    private double cost;
    private List<String> assignedProviders;

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

    public List<String> getAssignedProviders() {
        return assignedProviders;
    }

    public void AddAssignedProviders(String provider)
    {
        assignedProviders.add(provider);
    }

    //    public void addSubplan(Provider provider, Relation relation, double cost)
//    {
//        int hashCode = provider.hashCode() + relation.hashCode();
//        if (subplanMap.containsKey(hashCode))
//        {
//            if (subplanMap.get(hashCode) > cost)
//                subplanMap.put(hashCode, cost);
//        }
//        else
//            subplanMap.put(hashCode, cost);
//
//    }


}
