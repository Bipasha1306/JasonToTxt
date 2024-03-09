import graphql.language.*;
import graphql.parser.Parser;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class YamlExtractor {

    public static void main(String[] args) throws FileNotFoundException {
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
                processFields(selectionSet);
            } else {
                System.out.println("No query operation found in the GraphQL document.");
            }


    }

        private static void processFields(SelectionSet selectionSet) {
            for (Selection selection : selectionSet.getSelections()) {
                if (selection instanceof Field) {
                    Field field = (Field) selection;

                    // Access field information
                    String fieldName = field.getName();
                    System.out.println("Field Name: " + fieldName);

                    // If the field has subfields, recursively process them
                    if (field.getSelectionSet() != null) {
                        System.out.println("Subfields:");
                        processFields(field.getSelectionSet());
                    }
                }
            }
        }
    }