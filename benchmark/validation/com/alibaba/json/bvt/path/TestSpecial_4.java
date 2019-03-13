package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


public class TestSpecial_4 extends TestCase {
    public void test_special() throws Exception {
        String json = "{\"\u5927\u5c0f\":123}";
        JSONObject object = JSON.parseObject(json);
        Object obj = JSONPath.eval(object, "$.??");
        TestCase.assertEquals(123, obj);
    }
}

