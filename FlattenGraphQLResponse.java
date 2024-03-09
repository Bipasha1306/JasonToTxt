import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FlattenGraphQLResponse {

    public static void main(String[] args) {
        // Replace "your-file.yml" with the actual YAML file path
        String filePath = "application.yml";

        // Read YAML file into a Map
        Map<String, String> yamlData = readYAMLFile(filePath);

        // Convert YAML data to JSON for both query and response

        String queryJsonData = (String) yamlData.get("graphqlQuery");
        String responseJsonData = (String) yamlData.get("graphqlResponse");

        JsonNode queryJsonNode = parseJSON(queryJsonData);
        JsonNode responseJsonNode = parseJSON(responseJsonData);

        // Map GraphQL response to JSON structure with empty placeholders
        Map<String, Object> mappedData = mapToJSONStructure(queryJsonNode, responseJsonNode);

        // Flatten JSON data
        Map<String, String> flattenedData = flattenJSON(mappedData);

        // Print flattened data to console
        flattenedData.forEach((key, value) -> System.out.println(key + ": " + value));

        // Write flattened data to a text file
        writeToFile(flattenedData, "output.txt");
    }

    private static Map<String, String> readYAMLFile(String filePath) {
        // Load YAML file into Map
        try (InputStream inputStream = FlattenGraphQLResponse.class.getClassLoader().getResourceAsStream(filePath);
             InputStreamReader streamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(streamReader)) {

            StringBuilder yamlContent = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                yamlContent.append(line).append("\n");
            }

            Yaml yaml = new Yaml();
            return yaml.load(yamlContent.toString());

        } catch (IOException e) {
            throw new RuntimeException("Error reading YAML file", e);
        }
    }

    private static JsonNode parseJSON(String jsonData) {
        // Parse JSON data
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonData);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON data", e);
        }
    }

    private static Map<String, Object> mapToJSONStructure(JsonNode queryJsonNode, JsonNode responseJsonNode) {
        // Map GraphQL response to JSON structure with empty placeholders
        Map<String, Object> mappedData = new LinkedHashMap<>();
        mapJsonNodes("", queryJsonNode, responseJsonNode, mappedData);
        return mappedData;
    }

    private static void mapJsonNodes(String prefix, JsonNode queryJsonNode, JsonNode responseJsonNode,
                                     Map<String, Object> mappedData) {
        if (queryJsonNode.isObject()) {
            queryJsonNode.fields().forEachRemaining(entry -> {
                String fieldName = entry.getKey();
                JsonNode queryFieldValue = entry.getValue();
                JsonNode responseFieldValue = responseJsonNode.path(fieldName);

                mapJsonNodes(prefix + fieldName + ".", queryFieldValue, responseFieldValue, mappedData);
            });
        } else {
            // For leaf nodes, use the response value if available, otherwise use an empty placeholder
            String value = responseJsonNode.isMissingNode() ? "" : responseJsonNode.asText();
            mappedData.put(prefix.substring(0, prefix.length() - 1), value);
        }
    }

    private static Map<String, String> flattenJSON(Map<String, Object> mappedData) {
        // Flatten JSON data
        Map<String, String> flattenedData = new LinkedHashMap<>();
        flattenMap("", mappedData, flattenedData);
        return flattenedData;
    }

    private static void flattenMap(String prefix, Map<String, Object> map, Map<String, String> flattenedData) {
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                flattenMap(prefix + key + ".", (Map<String, Object>) value, flattenedData);
            } else {
                flattenedData.put(prefix + key, value.toString());
            }
        });
    }

    private static void writeToFile(Map<String, String> data, String filePath) {
        // Write flattened data to a text file
        try (PrintWriter writer = new PrintWriter(filePath)) {
            data.forEach((key, value) -> writer.println(key + ": " + value));
            System.out.println("Data written to " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file", e);
        }
    }
}
