package CostModel;

import ConfigurationParser.Provider;
import RelationProfileTreeBuilder.Relation;

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

    public void addSubplan(Provider provider, Relation relation, double cost)
    {
        int hashCode = provider.hashCode() + relation.hashCode();
        if (subplanMap.containsKey(hashCode))
        {
            if (subplanMap.get(hashCode) > cost)
                subplanMap.put(hashCode, cost);
        }
        else
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
