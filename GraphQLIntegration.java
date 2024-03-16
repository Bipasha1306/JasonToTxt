import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.language.Document;
import graphql.language.OperationDefinition;
import graphql.language.Field;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.parser.Parser;

import java.util.*;

public class GraphQLIntegration {

    public static void main(String[] args) throws Exception {
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

        String jsonString = "{ \"data\": { \"accountByIds\": [{ \"accountId\": 23980, \"profitabilityChannel\": null, \"subBusinessSegment\": { \"code\": \"MFOS\" }, \"comment\": \"Rebranded 13/09/05.\", \"spn\": \"2508346\", \"accountLegal\": { \"primaryJpmLegalEntity\": { \"code\": \"JP\", \"name\": \"J.P. Morgan Investment Management Inc.\" }, \"discretionaryAuthority\": { \"code\": \"1\" }, \"stateResident\": null, \"legalClassification\": { \"code\": \"SICA\" }, \"delegatedJpmRoleEntity\": { \"code\": \"IM\", \"name\": \"Investment Manager\" }, \"typeOfAgreement\": null, \"governingLawCountry\": { \"code\": \"LU\", \"name\": \"Luxembourg\" }, \"delegatedJpmLegalEntity\": { \"code\": \"JF\", \"name\": \"JPMorgan Asset Management (Asia Pacific) Limited\" }, \"subDelegatedJpmLegalEntity\": null, \"fundManagerPartyId\": 43157, \"fundRegulatoryLawCd\": { \"code\": \"13\", \"name\": \"UCITS Lux\" } } }] }}";

        Map<String, List<Object>> outputMap = parseGraphQLQuery(query);
        updateOutputMapWithResponse(jsonString, outputMap);
        printOutput(outputMap);
    }

    private static Map<String, List<Object>> parseGraphQLQuery(String query) {
        Map<String, List<Object>> outputMap = new LinkedHashMap<>();
        Document document = new Parser().parseDocument(query);

        for (OperationDefinition definition : document.getDefinitionsOfType(OperationDefinition.class)) {
            SelectionSet selectionSet = definition.getSelectionSet();
            processSelectionSet(selectionSet, "", outputMap);
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

    private static void updateOutputMapWithResponse(String jsonString, Map<String, List<Object>> outputMap) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString).get("data");

        if (rootNode != null && rootNode.isObject()) {
            JsonNode accountByIdsNode = rootNode.get("accountByIds");
            if (accountByIdsNode != null && accountByIdsNode.isArray() && accountByIdsNode.size() > 0) {
                JsonNode firstAccountNode = accountByIdsNode.get(0); // Assuming only one account is returned
                if (firstAccountNode != null && firstAccountNode.isObject()) {
                    processJsonNode(firstAccountNode, "accountByIds", outputMap);
                }
            }
        }
    }

    private static void processJsonNode(JsonNode node, String parentPath, Map<String, List<Object>> outputMap) {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fieldsIterator.next();
            String fieldName = fieldEntry.getKey();
            JsonNode fieldValue = fieldEntry.getValue();

            if (fieldValue.isArray()) {
                List<Object> subList = new ArrayList<>();
                for (JsonNode arrayItem : fieldValue) {
                    if (arrayItem.isObject()) {
                        processJsonObject(arrayItem, parentPath + "." + fieldName, outputMap);
                    } else {
                        subList.add(arrayItem.asText());
                    }
                }
                outputMap.put(parentPath + "." + fieldName, subList);
            } else if (fieldValue.isObject()) {
                processJsonObject(fieldValue, parentPath + "." + fieldName, outputMap);
            } else {
                List<Object> fieldValues = new ArrayList<>();
                fieldValues.add(fieldValue.asText());
                outputMap.put(parentPath + "." + fieldName, fieldValues);
            }
        }
    }

    private static void processJsonObject(JsonNode node, String parentPath, Map<String, List<Object>> outputMap) {
        Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String, JsonNode> fieldEntry = fieldsIterator.next();
            String fieldName = fieldEntry.getKey();
            JsonNode fieldValue = fieldEntry.getValue();

            if (fieldValue.isArray()) {
                List<Object> subList = new ArrayList<>();
                for (JsonNode arrayItem : fieldValue) {
                    if (arrayItem.isObject()) {
                        processJsonObject(arrayItem, parentPath + "." + fieldName, outputMap);
                    } else {
                        subList.add(arrayItem.asText());
                    }
                }
                outputMap.put(parentPath + "." + fieldName, subList);
            } else if (fieldValue.isObject()) {
                processJsonObject(fieldValue, parentPath + "." + fieldName, outputMap);
            } else {
                List<Object> fieldValues = new ArrayList<>();
                fieldValues.add(fieldValue.asText());
                outputMap.put(parentPath + "." + fieldName, fieldValues);
            }
        }
    }

    private static void printOutput(Map<String, List<Object>> outputMap) {
        for (Map.Entry<String, List<Object>> entry : outputMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
