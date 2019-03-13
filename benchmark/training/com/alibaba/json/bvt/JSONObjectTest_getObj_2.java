package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONObjectTest_getObj_2 extends TestCase {
    public void test_get_empty() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("value", "");
        Assert.assertEquals("", obj.get("value"));
        Assert.assertNull(obj.getObject("value", JSONObjectTest_getObj_2.Model.class));
    }

    public void test_get_null() throws Exception {
        TypeUtils.cast("null", JSONObjectTest_getObj_2.getType(), ParserConfig.getGlobalInstance());
        TypeUtils.cast("", JSONObjectTest_getObj_2.getType(), ParserConfig.getGlobalInstance());
    }

    public static class Model {}
}

