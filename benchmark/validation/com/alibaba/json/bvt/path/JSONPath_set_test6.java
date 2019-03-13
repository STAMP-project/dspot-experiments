package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


public class JSONPath_set_test6 extends TestCase {
    public void test_jsonpath_1() throws Exception {
        JSONObject aa = new JSONObject();
        aa.put("app-a", "haj ");
        JSONPath.set(aa, "$.app\\-a\\.x", "123");
        TestCase.assertEquals("haj ", aa.getString("app-a"));
        TestCase.assertEquals("123", aa.getString("app-a.x"));
    }
}

