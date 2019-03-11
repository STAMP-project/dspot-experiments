package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONObjectTest_getObj extends TestCase {
    public void test_get_empty() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("value", "");
        Assert.assertEquals("", obj.get("value"));
        Assert.assertNull(obj.getObject("value", JSONObjectTest_getObj.Model.class));
    }

    public void test_get_null() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("value", "null");
        Assert.assertEquals("null", obj.get("value"));
        Assert.assertNull(obj.getObject("value", JSONObjectTest_getObj.Model.class));
    }

    public static class Model {}
}

