package com.baeldung.jsonobject.iterate;


import java.util.Map;
import org.json.JSONObject;
import org.junit.Test;


public class JSONObjectIteratorUnitTest {
    private JSONObjectIterator jsonObjectIterator = new JSONObjectIterator();

    @Test
    public void givenJSONObject_whenIterating_thenGetKeyValuePairs() {
        JSONObject jsonObject = getJsonObject();
        jsonObjectIterator.handleJSONObject(jsonObject);
        Map<String, Object> keyValuePairs = jsonObjectIterator.getKeyValuePairs();
        assertThat(keyValuePairs.get("rType")).isEqualTo("Regular");
        assertThat(keyValuePairs.get("rId")).isEqualTo("1001");
        assertThat(keyValuePairs.get("cType")).isEqualTo("Chocolate");
        assertThat(keyValuePairs.get("cId")).isEqualTo("1002");
        assertThat(keyValuePairs.get("bType")).isEqualTo("BlueBerry");
        assertThat(keyValuePairs.get("bId")).isEqualTo("1003");
        assertThat(keyValuePairs.get("name")).isEqualTo("Cake");
        assertThat(keyValuePairs.get("cakeId")).isEqualTo("0001");
        assertThat(keyValuePairs.get("type")).isEqualTo("donut");
        assertThat(keyValuePairs.get("Type")).isEqualTo("Maple");
        assertThat(keyValuePairs.get("tId")).isEqualTo("5001");
        assertThat(keyValuePairs.get("batters").toString()).isEqualTo("[{\"rType\":\"Regular\",\"rId\":\"1001\"},{\"cType\":\"Chocolate\",\"cId\":\"1002\"},{\"bType\":\"BlueBerry\",\"bId\":\"1003\"}]");
        assertThat(keyValuePairs.get("cakeShapes").toString()).isEqualTo("[\"square\",\"circle\",\"heart\"]");
        assertThat(keyValuePairs.get("topping").toString()).isEqualTo("{\"Type\":\"Maple\",\"tId\":\"5001\"}");
    }
}

