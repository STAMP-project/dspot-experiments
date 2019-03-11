package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class JSONObjectTest6 extends TestCase {
    public void test() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", 123);
        JSONObjectTest6.Model model = jsonObject.toJavaObject(JSONObjectTest6.Model.class);
        TestCase.assertEquals(123, model.value);
    }

    public static class Model {
        public int value;
    }
}

