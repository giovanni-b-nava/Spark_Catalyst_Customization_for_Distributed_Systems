package ConfigurationParser;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Configurator
{

    // Returns the list of providers with all the data taken from the configuration file .json
    public List<Provider> parse(String jsonFile) throws FileNotFoundException {

        // Support objects
        List<Provider> list = new ArrayList<>();
        Gson g = new Gson();

        // Read and parse the json file
        JsonReader reader = new JsonReader(new FileReader(jsonFile));
        JsonElement jelement = new JsonParser().parse(reader);

        // Read and add the providers to the list
        JsonObject jobject = jelement.getAsJsonObject();
        JsonArray providers = jobject.getAsJsonArray("providers");
        int i = 0;
        while(i < providers.size()) {
            JsonObject provider = providers.get(i).getAsJsonObject();
            Provider n = g.fromJson(provider, Provider.class);
            list.add(n);
            i++;
        }
        return list;
    }
}
