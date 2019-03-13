package com.baeldung.jsonpath.introduction;


import com.jayway.jsonpath.JsonPath;
import java.io.File;
import java.util.Map;
import net.minidev.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;


public class JsonPathUnitTest {
    private static String json;

    private static File jsonFile = new File("src/main/resources/online_store.json");

    @Test
    public void shouldMatchCountOfObjects() {
        Map<String, String> objectMap = JsonPath.read(JsonPathUnitTest.json, "$");
        Assert.assertEquals(3, objectMap.keySet().size());
    }

    @Test
    public void shouldMatchCountOfArrays() {
        JSONArray jsonArray = JsonPath.read(JsonPathUnitTest.json, "$.items.book[*]");
        Assert.assertEquals(2, jsonArray.size());
    }
}

