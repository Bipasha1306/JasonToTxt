import org.json.JSONObject;

public class NewResponse {

    public static void main(String[] args) {
        JSONObject json1 = new JSONObject("{\"accountByIds\":{\"managementResponsibilityCenter\":{\"code\":\"EQL\",\"name\":\"Equity JPM London\"},\"investmentSubVehicle\":{\"code\":\"SEPM\",\"investmentVehicle\":{\"code\":\"SEPM\"}},\"profitabilityChannel\":null}}");
        JSONObject json2 = new JSONObject("{\"accountByIds\":{\"managementResponsibilityCenter\":{\"code\":{},\"name\":{}},\"investmentSubVehicle\":{\"code\":{},\"investmentVehicle\":{\"code\":{}}},\"profitabilityChannel\":{\"code\":{},\"name\":{}}}}");

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
