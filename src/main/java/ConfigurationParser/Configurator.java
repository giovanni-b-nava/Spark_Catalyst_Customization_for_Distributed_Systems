package ConfigurationParser;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spark on 09/11/2016.
 */
public class Configurator {

    // Returns the list of nodes with all the data taken from the configuration file .json
    public List<Node> parse(String jsonFile) throws FileNotFoundException {

        // Support objects
        List<Node> list = new ArrayList<>();
        Gson g = new Gson();

        // Read and parse the json file
        JsonReader reader = new JsonReader(new FileReader(jsonFile));
        JsonElement jelement = new JsonParser().parse(reader);

        // Read and add the nodes to the list
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray nodes = jobject.getAsJsonArray("nodes");
        int i = 0;
        while(i < nodes.size()) {
            JsonObject node = nodes.get(i).getAsJsonObject();
            Node n = g.fromJson(node, Node.class);
            list.add(n);
            i++;
        }
        return list;
    }
}
