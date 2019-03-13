package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class JSONObjectTest7 extends TestCase {
    public void test() throws Exception {
        JSONObject jsonObject = JSON.parseObject("{\"test\":null,\"a\":\"cc\"}");
        TestCase.assertEquals(2, jsonObject.entrySet().size());
        TestCase.assertTrue(jsonObject.containsKey("test"));
    }
}

