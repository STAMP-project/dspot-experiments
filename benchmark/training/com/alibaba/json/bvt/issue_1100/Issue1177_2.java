package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import java.util.Map;
import junit.framework.TestCase;


/**
 * Created by wenshao on 05/05/2017.
 */
public class Issue1177_2 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "{\"a\":{\"x\":\"y\"},\"b\":{\"x\":\"y\"}}";
        Map<String, Issue1177_2.Model> jsonObject = JSONObject.parseObject(text, new com.alibaba.fastjson.TypeReference<Map<String, Issue1177_2.Model>>() {});
        System.out.println(JSON.toJSONString(jsonObject));
        String jsonpath = "$..x";
        String value = "y2";
        JSONPath.set(jsonObject, jsonpath, value);
        TestCase.assertEquals("{\"a\":{\"x\":\"y2\"},\"b\":{\"x\":\"y2\"}}", JSON.toJSONString(jsonObject));
    }

    public static class Model {
        public String x;
    }
}

