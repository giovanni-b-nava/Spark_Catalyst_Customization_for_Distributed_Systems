package CostModel;

import ConfigurationParser.Node;
import RelationProfileTreeBuilder.Relation;
import spire.math.algebraic.Sub;

import java.util.HashMap;

/**
 * Created by Giovanni on 24/11/2016.
 */
public class SubplanMap
{
    private HashMap<Integer, Integer> subplanMap;

    public SubplanMap()
    {
        subplanMap = new HashMap<>();
    }

    public void addSubplan(Node node, Relation relation, int cost)
    {
        int hashCode = node.hashCode() + relation.hashCode();
        if (subplanMap.containsKey(hashCode) && subplanMap.get(hashCode) > cost)
            subplanMap.put(hashCode, cost);
    }

    public HashMap<Integer, Integer> getSubplanMap()
    {
        return subplanMap;
    }

}
