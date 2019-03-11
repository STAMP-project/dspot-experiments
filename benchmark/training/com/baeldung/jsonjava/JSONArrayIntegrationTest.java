package com.baeldung.jsonjava;


import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


public class JSONArrayIntegrationTest {
    @Test
    public void givenJSONJava_thenCreateNewJSONArrayFromScratch() {
        JSONArray ja = new JSONArray();
        ja.put(Boolean.TRUE);
        ja.put("lorem ipsum");
        // We can also put a JSONObject in JSONArray
        JSONObject jo = new JSONObject();
        jo.put("name", "jon doe");
        jo.put("age", "22");
        jo.put("city", "chicago");
        ja.put(jo);
        Assert.assertEquals("[true,\"lorem ipsum\",{\"city\":\"chicago\",\"name\":\"jon doe\",\"age\":\"22\"}]", ja.toString());
    }

    @Test
    public void givenJsonString_thenCreateNewJSONArray() {
        JSONArray ja = new JSONArray("[true, \"lorem ipsum\", 215]");
        Assert.assertEquals("[true,\"lorem ipsum\",215]", ja.toString());
    }

    @Test
    public void givenListObject_thenConvertItToJSONArray() {
        List<String> list = new ArrayList<>();
        list.add("California");
        list.add("Texas");
        list.add("Hawaii");
        list.add("Alaska");
        JSONArray ja = new JSONArray(list);
        Assert.assertEquals("[\"California\",\"Texas\",\"Hawaii\",\"Alaska\"]", ja.toString());
    }
}

