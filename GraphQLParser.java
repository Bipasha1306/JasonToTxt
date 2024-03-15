import graphql.language.*;
import graphql.parser.Parser;

import java.util.*;

public class GraphQLParser {

    public static void main(String[] args) {
        String query = "query { " +
                "accountByIds(id: [129583, 23980]) { " +
                "accountId " +
                "primarySystem { " +
                "code " +
                "} " +
                "code " +
                "longName " +
                "shortName " +
                "briefName " +
                "profitabilityChannel { " +
                "code " +
                "name " +
                "} " +
                "accountStatusType { " +
                "code " +
                "name " +
                "} " +
                "accountType { " +
                "code " +
                "} " +
                "startDate " +
                "endDate " +
                "fundingEvent { " +
                "intendedFundingDate " +
                "date " +
                "} " +
                "managementResponsibilityCenter { " +
                "code " +
                "name " +
                "} " +
                "investmentSubVehicle { " +
                "code " +
                "investmentVehicle { " +
                "code " +
                "} " +
                "} " +
                "subBusinessSegment { " +
                "code " +
                "} " +
                "comment " +
                "spn " +
                "} " +
                "} ";

        Map<String, List<Object>> outputMap = parseGraphQLQuery(query);
        printOutput(outputMap);
    }

    private static Map<String, List<Object>> parseGraphQLQuery(String query) {
        Map<String, List<Object>> outputMap = new LinkedHashMap<>();
        Document document = new Parser().parseDocument(query);

        for (Definition definition : document.getDefinitions()) {
            if (definition instanceof OperationDefinition) {
                SelectionSet selectionSet = ((OperationDefinition) definition).getSelectionSet();
                processSelectionSet(selectionSet, "", outputMap);
            }
        }

        return outputMap;
    }

    private static void processSelectionSet(SelectionSet selectionSet, String parentPath, Map<String, List<Object>> outputMap) {
        for (Selection selection : selectionSet.getSelections()) {
            if (selection instanceof Field) {
                Field field = (Field) selection;
                String fieldName = parentPath.isEmpty() ? field.getName() : parentPath + "." + field.getName();
                List<Object> fieldValues = new ArrayList<>();
                fieldValues.add(null); // Add null value for the field
                outputMap.put(fieldName, fieldValues);
                if (field.getSelectionSet() != null) {
                    processSelectionSet(field.getSelectionSet(), fieldName, outputMap);
                }
            }
        }
    }

    private static void printOutput(Map<String, List<Object>> outputMap) {
        for (Map.Entry<String, List<Object>> entry : outputMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
