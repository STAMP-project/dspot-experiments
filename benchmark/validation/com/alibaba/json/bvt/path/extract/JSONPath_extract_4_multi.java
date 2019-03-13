package com.alibaba.json.bvt.path.extract;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


public class JSONPath_extract_4_multi extends TestCase {
    public void test_0() throws Exception {
        TestCase.assertEquals("[\"male\",\"john\"]", JSONPath.extract(JSONPath_extract_4_multi.json, "$[0]['gender','name']").toString());
    }

    public void test_1() throws Exception {
        TestCase.assertEquals("[\"john\",\"male\"]", JSONPath.extract(JSONPath_extract_4_multi.json, "$[0]['name','gender']").toString());
    }

    private static final String json = "[\n" + ((((((("   {\n" + "      \"name\" : \"john\",\n") + "      \"gender\" : \"male\"\n") + "   },\n") + "   {\n") + "      \"name\" : \"ben\"\n") + "   }\n") + "]");
}

