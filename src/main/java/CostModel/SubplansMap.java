package CostModel;

import ConfigurationParser.Node;
import RelationProfileTreeBuilder.Relation;
import spire.math.algebraic.Sub;

import java.util.HashMap;

/**
 * Created by Giovanni on 24/11/2016.
 */
public class SubplansMap
{
    private HashMap<Integer, Double> subplanMap;

    public SubplansMap()
    {
        subplanMap = new HashMap<>();
    }

    public void addSubplan(Node provider, Relation relation, double cost)
    {
        int hashCode = provider.hashCode() + relation.hashCode();
        if (subplanMap.containsKey(hashCode) && subplanMap.get(hashCode) > cost)
            subplanMap.put(hashCode, cost);
    }

    public HashMap<Integer, Double> getSubplanMap()
    {
        return subplanMap;
    }

    @Override
    public String toString() {
        return subplanMap.toString();
    }
}
