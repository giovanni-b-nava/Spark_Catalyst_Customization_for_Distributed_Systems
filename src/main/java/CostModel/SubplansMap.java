package CostModel;

import ConfigurationParser.Provider;
import RelationProfileTreeBuilder.Relation;

import java.util.HashMap;

/**
 * Created by Giovanni on 24/11/2016.
 */
public class SubplansMap
{
    private HashMap<Integer, Plan> subplanMap;

    public SubplansMap()
    {
        subplanMap = new HashMap<>();
    }

    public void addSubplan(Plan plan)
    {
        int hashCode = plan.hashCode();
        if (subplanMap.containsKey(hashCode))
        {
            if (subplanMap.get(hashCode).getCost() > plan.getCost())
                subplanMap.put(hashCode, plan);
        }
        else
            subplanMap.put(hashCode, plan);

    }

    public HashMap<Integer, Plan> getSubplanMap()
    {
        return subplanMap;
    }

    @Override
    public String toString() {
        return subplanMap.toString();
    }
}