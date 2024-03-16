import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GraphqlResponse {
    public static void main(String[] args) throws Exception {
        String jsonString = "{ \"data\": { \"accountByIds\": [{ \"accountId\": 23980, \"profitabilityChannel\": null, \"subBusinessSegment\": { \"code\": \"MFOS\" }, \"comment\": \"Rebranded 13/09/05.\", \"spn\": \"2508346\", \"accountLegal\": { \"primaryJpmLegalEntity\": { \"code\": \"JP\", \"name\": \"J.P. Morgan Investment Management Inc.\" }, \"discretionaryAuthority\": { \"code\": \"1\" }, \"stateResident\": null, \"legalClassification\": { \"code\": \"SICA\" }, \"delegatedJpmRoleEntity\": { \"code\": \"IM\", \"name\": \"Investment Manager\" }, \"typeOfAgreement\": null, \"governingLawCountry\": { \"code\": \"LU\", \"name\": \"Luxembourg\" }, \"delegatedJpmLegalEntity\": { \"code\": \"JF\", \"name\": \"JPMorgan Asset Management (Asia Pacific) Limited\" }, \"subDelegatedJpmLegalEntity\": null, \"fundManagerPartyId\": 43157, \"fundRegulatoryLawCd\": { \"code\": \"13\", \"name\": \"UCITS Lux\" } } }] }}";

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString).get("data");

        Map<String, Object> resultMap = new HashMap<>();
        processJsonNode(rootNode, resultMap);

        System.out.println(resultMap);
    }

    private static void processJsonNode(JsonNode node, Map<String, Object> resultMap) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = node.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> fieldEntry = fieldsIterator.next();
                String fieldName = fieldEntry.getKey();
                JsonNode fieldValue = fieldEntry.getValue();

                if (fieldValue.isObject() || fieldValue.isArray()) {
                    if (fieldValue.isArray() && fieldValue.size() > 0) {
                        resultMap.put(fieldName, processArrayNode(fieldValue.get(0)));
                    } else {
                        resultMap.put(fieldName, processObjectNode(fieldValue));
                    }
                } else {
                    resultMap.put(fieldName, fieldValue.asText());
                }
            }
        }
    }

    private static Object processObjectNode(JsonNode node) {
        Map<String, Object> resultMap = new HashMap<>();
        processJsonNode(node, resultMap);
        return resultMap;
    }

    private static Object processArrayNode(JsonNode node) {
        Map<String, Object> resultMap = new HashMap<>();
        processJsonNode(node, resultMap);
        return resultMap;
    }
}
