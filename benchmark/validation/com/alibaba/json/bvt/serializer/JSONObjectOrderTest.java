package com.alibaba.json.bvt.serializer;


import JSON.VERSION;
import SerializerFeature.MapSortField;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class JSONObjectOrderTest extends TestCase {
    public void test_for_order() throws Exception {
        System.out.println(VERSION);
        JSONObject jsonObj = new JSONObject(true);
        jsonObj.put("code", "code");
        jsonObj.put("msg", "msg");
        jsonObj.put("data", "data");
        String jsonStr = JSON.toJSONString(jsonObj, MapSortField);
        TestCase.assertEquals("{\"code\":\"code\",\"msg\":\"msg\",\"data\":\"data\"}", jsonStr);
    }
}

