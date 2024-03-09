import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RemoveSquareBrackets {
    public static void main(String[] args) throws Exception {
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
                "}";

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
    }
}
