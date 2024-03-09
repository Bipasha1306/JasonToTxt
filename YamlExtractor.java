import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.parser.Parser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import graphql.language.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class YamlExtractor {

    public static void main(String[] args) throws IOException {
        String filePath = "application.yml"; // Replace with the actual path to your YAML file
        String placeholder = "graphqlQuery";

        // Load YAML file
        Yaml yaml = new Yaml();
        FileInputStream inputStream = new FileInputStream(filePath);
        Map<String, Object> yamlData = yaml.load(inputStream);

        // Extract content based on the placeholder
        String graphqlQuery = null;
        if (yamlData.containsKey(placeholder)) {
            graphqlQuery = (String) yamlData.get(placeholder);
            System.out.println("GraphQL Query:\n" + graphqlQuery);
        } else {
            System.out.println("Placeholder not found in the YAML file.");
        }

        // Parse GraphQL query
        Document document = new Parser().parseDocument(graphqlQuery);

        // Access the first operation (assumes it's a query)
        OperationDefinition operationDefinition = document.getDefinitions().stream()
                .filter(def -> def instanceof OperationDefinition)
                .map(def -> (OperationDefinition) def)
                .findFirst()
                .orElse(null);

        if (operationDefinition != null) {
            SelectionSet selectionSet = operationDefinition.getSelectionSet();

            // Process each field in the selection set
            JSONObject json1 = processFields(selectionSet);

            // Convert JSON object to string
            String response1 = json1.toString();
            System.out.println("JSON Representation:\n" + response1);

            String response2 = "{\n" +
                    "    \"accountByIds\": [\n" +
                    "      {\n" +
                    "        \"accountId\": 23980,\n" +
                    "        \"primarySystem\": {\n" +
                    "          \"code\": \"WSSL\"\n" +
                    "        },\n" +
                    "        \"code\": \"11247\",\n" +
                    "        \"longName\": \"11247 - 2 - long nm\",\n" +
                    "        \"shortName\": \"11247 -2- short nm\",\n" +
                    "        \"briefName\": \"11247 -2\",\n" +
                    "        \"accountStatusType\": {\n" +
                    "          \"code\": \"A\",\n" +
                    "          \"name\": \"Active\"\n" +
                    "        },\n" +
                    "        \"accountType\": {\n" +
                    "          \"code\": \"N\"\n" +
                    "        },\n" +
                    "        \"startDate\": \"1997-11-19\",\n" +
                    "        \"endDate\": null,\n" +
                    "        \"fundingEvent\": {\n" +
                    "          \"intendedFundingDate\": null,\n" +
                    "          \"date\": null\n" +
                    "        },\n" +
                    "        \"managementResponsibilityCenter\": {\n" +
                    "          \"code\": \"EQL\",\n" +
                    "          \"name\": \"Equity JPM London\"\n" +
                    "        },\n" +
                    "        \"investmentSubVehicle\": {\n" +
                    "          \"code\": \"SEPM\",\n" +
                    "          \"investmentVehicle\": {\n" +
                    "            \"code\": \"SEPM\"\n" +
                    "          }\n" +
                    "        },\n" +
                    "        \"profitabilityChannel\": null,\n" +
                    "        \"subBusinessSegment\": {\n" +
                    "          \"code\": \"MFOS\"\n" +
                    "        }\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }";

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response2);

            // Get the content of the "accountByIds" array and convert it to an object
            JsonNode accountByIds = jsonNode.get("accountByIds").get(0);

            // Remove the "accountByIds" array from the original JSON
            ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).remove("accountByIds");

            // Add the content of "accountByIds" as an object under "accountByIds" key
            ((com.fasterxml.jackson.databind.node.ObjectNode) jsonNode).set("accountByIds", accountByIds);

            // Convert the modified JSON back to a string
            String result = objectMapper.writeValueAsString(jsonNode);

            System.out.println(result);
            JSONObject json2 = new JSONObject(result);
            System.out.println(result);

            System.out.println("Response 1: " + json1.toString());
            System.out.println("Response 2: " + json2.toString());
            // Create a new JSON object for the result
            JSONObject json3 = createMergedJson(json2, json1);

            // Print the result
            System.out.println("Response 3: " + json3.toString());

        }
    }

    private static JSONObject createMergedJson(JSONObject json2, JSONObject json1) {
        JSONObject mergedJson = new JSONObject();

        // Iterate through fields in response2
        for (String key : json2.keySet()) {
            if (json1.has(key)) {
                mergedJson.put(key, updateJson(json2.get(key), json1.get(key)));
            } else {
                mergedJson.put(key, json2.get(key));
            }
        }

        // Add any additional fields from response1
        for (String key : json1.keySet()) {
            if (!mergedJson.has(key)) {
                mergedJson.put(key, json1.get(key));
            }
        }

        // Remove square brackets from the entire json2 object using regex
        String jsonString = json2.toString();
        String regex = "\\[|\\]"; // Matches square brackets
        String modifiedJsonString = jsonString.replaceAll(regex, "");

        // Parse the modified JSON string back to a JSONObject
        try {
            JSONObject modifiedJson2 = new JSONObject(modifiedJsonString);
            return modifiedJson2;
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception (e.g., log an error, return the original json2, etc.)
            return json2;
        }
    }



    private static Object updateJson(Object json2, Object json1) {
        JSONObject updatedJson = null;
        if (json2 instanceof JSONObject && json1 instanceof JSONObject) {
            updatedJson = new JSONObject();
            JSONObject jsonObj2 = (JSONObject) json2;
            JSONObject jsonObj1 = (JSONObject) json1;

            for (String key : jsonObj2.keySet()) {
                if (jsonObj1.has(key)) {
                    updatedJson.put(key, updateJson(jsonObj2.get(key), jsonObj1.get(key)));
                } else if (jsonObj2.isNull(key)) {
                    updatedJson.put(key, JSONObject.NULL);
                } else {
                    updatedJson.put(key, jsonObj2.get(key));
                }
            }

            return updatedJson;
        } else if (json2 instanceof JSONArray && json1 instanceof JSONObject) {
            // Convert JSONArray to JSONObject if it has a single element
            JSONArray jsonArray2 = (JSONArray) json2;
            if (jsonArray2.length() == 1 && jsonArray2.get(0) instanceof JSONObject) {
                return (JSONObject) jsonArray2.get(0);
            }
        } else if (json2 instanceof JSONArray && json1 instanceof JSONArray) {
            JSONArray updatedArray = new JSONArray();
            JSONArray jsonArray2 = (JSONArray) json2;
            JSONArray jsonArray1 = (JSONArray) json1;

            for (int i = 0; i < jsonArray2.length(); i++) {
                if (jsonArray1.length() > i) {
                    updatedArray.put(updateJson(jsonArray2.get(i), jsonArray1.get(i)));
                } else if (jsonArray2.isNull(i)) {
                    updatedArray.put(JSONObject.NULL);
                } else {
                    updatedArray.put(jsonArray2.get(i));
                }
            }

            return updatedArray;
        }

        return removeSquareBrackets(updatedJson);
    }
    private static JSONObject processFields(SelectionSet selectionSet) {
        // Create a JSON object
        JSONObject jsonObject = new JSONObject();

        for (Selection selection : selectionSet.getSelections()) {
            if (selection instanceof Field) {
                Field field = (Field) selection;

                // Access field information
                String fieldName = field.getName();

                // If the field has subfields, recursively process them
                if (field.getSelectionSet() != null) {
                    jsonObject.put(fieldName, processFields(field.getSelectionSet()));
                } else {
                    jsonObject.put(fieldName, new JSONObject()); // Empty object
                }
            }
        }

        return jsonObject;
    }
    private static Object removeSquareBrackets(Object json) {
        if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            if (jsonArray.length() == 1) {
                return removeSquareBrackets(jsonArray.get(0));
            } else {
                JSONArray updatedArray = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    updatedArray.put(removeSquareBrackets(jsonArray.get(i)));
                }
                return updatedArray;
            }
        } else if (json instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) json;
            JSONObject updatedObject = new JSONObject();
            for (String key : jsonObject.keySet()) {
                updatedObject.put(key, removeSquareBrackets(jsonObject.get(key)));
            }
            return updatedObject;
        }

        return (JSONObject) json;
    }


}
