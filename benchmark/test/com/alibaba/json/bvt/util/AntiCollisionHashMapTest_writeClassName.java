package com.alibaba.json.bvt.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.AntiCollisionHashMap;
import junit.framework.TestCase;


public class AntiCollisionHashMapTest_writeClassName extends TestCase {
    public void test_for_bug() throws Exception {
        AntiCollisionHashMapTest_writeClassName.Model m = JSON.parseObject("{\"value\":{\"@type\":\"com.alibaba.fastjson.util.AntiCollisionHashMap\"}}", AntiCollisionHashMapTest_writeClassName.Model.class);
        TestCase.assertTrue(((m.value.getInnerMap()) instanceof AntiCollisionHashMap));
    }

    public static class Model {
        public JSONObject value;
    }
}

