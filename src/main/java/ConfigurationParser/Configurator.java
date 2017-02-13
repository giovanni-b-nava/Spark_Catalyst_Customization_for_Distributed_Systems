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
        List<Provider> providerList = new ArrayList<>();
        Gson g = new Gson();

        // Read and parse the json file
        JsonReader jsonReader = new JsonReader(new FileReader(jsonFile));
        JsonElement jsonElement = new JsonParser().parse(jsonReader);

        // Read and add the providers to the list
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArrayProviders = jsonObject.getAsJsonArray("providers");

        int i = 0;
        while(i < jsonArrayProviders.size())
        {
            JsonObject provider = jsonArrayProviders.get(i).getAsJsonObject();
            Provider n = g.fromJson(provider, Provider.class);
            providerList.add(n);
            i++;
        }

        return providerList;
    }

}
