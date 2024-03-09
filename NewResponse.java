import org.json.JSONObject;

public class NewResponse {

    public static void main(String[] args) {
        JSONObject json1 = new JSONObject("{\"accountByIds\":{\"code\":\"11247\",\"accountStatusType\":{\"code\":\"A\",\"name\":\"Active\"},\"endDate\":null,\"profitabilityChannel\":null,\"briefName\":\"11247 -2\",\"accountType\":{\"code\":\"N\"},\"managementResponsibilityCenter\":{\"code\":\"EQL\",\"name\":\"Equity JPM London\"},\"accountId\":23980,\"investmentSubVehicle\":{\"code\":\"SEPM\",\"investmentVehicle\":{\"code\":\"SEPM\"}},\"primarySystem\":{\"code\":\"WSSL\"},\"fundingEvent\":{\"date\":null,\"intendedFundingDate\":null},\"subBusinessSegment\":{\"code\":\"MFOS\"},\"shortName\":\"11247 -2- short nm\",\"startDate\":\"1997-11-19\",\"longName\":\"11247 - 2 - long nm\"}}");
        JSONObject json2 = new JSONObject("{\"accountByIds\":{\"code\":{},\"accountStatusType\":{\"code\":{},\"name\":{}},\"endDate\":{},\"profitabilityChannel\":{\"code\":{},\"name\":{}},\"briefName\":{},\"accountType\":{\"code\":{}},\"managementResponsibilityCenter\":{\"code\":{},\"name\":{}},\"accountId\":{},\"investmentSubVehicle\":{\"code\":{},\"investmentVehicle\":{\"code\":{}}},\"primarySystem\":{\"code\":{}},\"fundingEvent\":{\"date\":{},\"intendedFundingDate\":{}},\"shortName\":{},\"startDate\":{},\"longName\":{}}}");

        JSONObject mergedJson = mergeJsons(json2, json1);
        System.out.println(mergedJson.toString(4));
    }

    private static JSONObject mergeJsons(JSONObject json2, JSONObject json1) {
        JSONObject mergedJson = new JSONObject();

        // Reverse the order of keys from json2
        for (String key : reverseOrder(json2.keySet())) {
            if (json1.has(key)) {
                if (json1.isNull(key)) {
                    mergedJson.put(key, json2.get(key));
                } else if (json1.get(key) instanceof JSONObject && json2.get(key) instanceof JSONObject) {
                    mergedJson.put(key, mergeJsons(json2.getJSONObject(key), json1.getJSONObject(key)));
                } else {
                    mergedJson.put(key, json1.get(key));
                }
            } else {
                mergedJson.put(key, json2.get(key));
            }
        }

        return mergedJson;
    }

    // Helper method to reverse the order of an Iterable
    private static Iterable<String> reverseOrder(Iterable<String> iterable) {
        java.util.List<String> list = new java.util.ArrayList<>();
        iterable.forEach(list::add);
        java.util.Collections.reverse(list);
        return list;
    }
}
