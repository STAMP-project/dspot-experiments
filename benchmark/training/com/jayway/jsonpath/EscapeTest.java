package com.jayway.jsonpath;


import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import org.junit.Test;


public class EscapeTest extends BaseTest {
    private static JSONStyle style;

    @Test
    public void urls_are_not_escaped() {
        JSONStyle orig = JSONValue.COMPRESSION;
        String json = "[" + ((("\"https://a/b/1\"," + "\"https://a/b/2\",") + "\"https://a/b/3\"") + "]");
        String resAsString = JsonPath.using(BaseTest.JSON_SMART_CONFIGURATION).parse(json).read("$").toString();
        assertThat(resAsString).contains("https://a/b/1");
    }
}

