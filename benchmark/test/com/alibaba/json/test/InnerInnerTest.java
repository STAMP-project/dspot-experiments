package com.alibaba.json.test;


import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import junit.framework.TestCase;


public class InnerInnerTest extends TestCase {
    // ???????????????
    public void testDeserialize() {
        // 
        String json = "{\"ii\":{\"name\":\"iicls\"},\"name\":\"ocls\"}";
        Outter o = JSON.parseObject(json, Outter.class);
        TestCase.assertEquals("ocls", o.getName());
        TestCase.assertEquals("iicls", o.getIi().getName());
    }

    public void testSerialize() {
        Outter o = new Outter();
        Outter.Inner i = o.new Inner();
        Outter.Inner.InnerInner ii = i.new InnerInner();
        ii.setName("iicls");
        o.setIi(ii);
        o.setName("ocls");
        String json = JSON.toJSONString(o);
        TestCase.assertEquals("{\"ii\":{\"name\":\"iicls\"},\"name\":\"ocls\"}", json);
    }

    public void testGson() {
        Outter o = new Outter();
        Outter.Inner i = o.new Inner();
        Outter.Inner.InnerInner ii = i.new InnerInner();
        ii.setName("iicls");
        o.setIi(ii);
        o.setName("ocls");
        Gson gson = new Gson();// default setting

        String json = gson.toJson(o);
        TestCase.assertEquals("{\"name\":\"ocls\",\"ii\":{\"name\":\"iicls\"}}", json);
        Outter newO = gson.fromJson(json, Outter.class);
        TestCase.assertEquals("ocls", newO.getName());
        TestCase.assertEquals("iicls", newO.getIi().getName());
    }
}

