package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import org.junit.Assert;


public class MapTest extends TestCase {
    public void test_no_sort() throws Exception {
        JSONObject obj = new JSONObject(true);
        obj.put("name", "jobs");
        obj.put("id", 33);
        String text = MapTest.toJSONString(obj);
        Assert.assertEquals("{'name':'jobs','id':33}", text);
    }

    public void test_null() throws Exception {
        JSONObject obj = new JSONObject(true);
        obj.put("name", null);
        String text = JSON.toJSONString(obj, WriteMapNullValue);
        Assert.assertEquals("{\"name\":null}", text);
    }
}

