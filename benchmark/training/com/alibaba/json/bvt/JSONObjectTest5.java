package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class JSONObjectTest5 extends TestCase {
    public void test() throws Exception {
        JSONObject jsonObject = new JSONObject(3, true);
        jsonObject.put("name", "J.K.SAGE");
        jsonObject.put("age", 21);
        jsonObject.put("msg", "Hello!");
        JSONObject cloneObject = ((JSONObject) (jsonObject.clone()));
        TestCase.assertEquals(JSON.toJSONString(jsonObject), JSON.toJSONString(cloneObject));
    }
}

