package com.alibaba.json.bvt.path.extract;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


public class JSONPath_extract_3 extends TestCase {
    public void test_0() throws Exception {
        TestCase.assertEquals("male", JSONPath.extract(JSONPath_extract_3.json, "$[0]['gender']").toString());
    }

    public void test_1() throws Exception {
        TestCase.assertNull(JSONPath.extract(JSONPath_extract_3.json, "$[1]['gender']"));
    }

    public void test_2() throws Exception {
        TestCase.assertEquals("ben", JSONPath.extract(JSONPath_extract_3.json, "$[1]['name']").toString());
    }

    private static final String json = "[\n" + ((((((("   {\n" + "      \"name\" : \"john\",\n") + "      \"gender\" : \"male\"\n") + "   },\n") + "   {\n") + "      \"name\" : \"ben\"\n") + "   }\n") + "]");
}

