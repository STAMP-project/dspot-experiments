package com.alibaba.json.bvt.issue_1100;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 05/05/2017.
 */
public class Issue1177_3 extends TestCase {
    public void test_for_issue() throws Exception {
        String text = "[{\"x\":\"y\"},{\"x\":\"y\"}]";
        List<Issue1177_3.Model> jsonObject = JSONObject.parseObject(text, new com.alibaba.fastjson.TypeReference<List<Issue1177_3.Model>>() {});
        System.out.println(JSON.toJSONString(jsonObject));
        String jsonpath = "$..x";
        String value = "y2";
        JSONPath.set(jsonObject, jsonpath, value);
        TestCase.assertEquals("[{\"x\":\"y2\"},{\"x\":\"y2\"}]", JSON.toJSONString(jsonObject));
    }

    public static class Model {
        public String x;
    }
}

