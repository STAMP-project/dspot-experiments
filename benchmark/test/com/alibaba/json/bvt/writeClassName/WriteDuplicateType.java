package com.alibaba.json.bvt.writeClassName;


import JSON.DEFAULT_TYPE_KEY;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteDuplicateType extends TestCase {
    public void test_dupType() throws Exception {
        WriteDuplicateType.DianDianCart cart = new WriteDuplicateType.DianDianCart();
        cart.setId(1001);
        LinkedHashMap<String, JSONObject> cartMap = new LinkedHashMap<String, JSONObject>();
        JSONObject obj = new JSONObject();
        obj.put("id", 1001);
        obj.put(DEFAULT_TYPE_KEY, "com.alibaba.json.bvt.writeClassName.WriteDuplicateType$DianDianCart");
        cartMap.put("1001", obj);
        String text1 = JSON.toJSONString(cartMap, WriteClassName);
        Assert.assertEquals("{\"@type\":\"java.util.LinkedHashMap\",\"1001\":{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteDuplicateType$DianDianCart\",\"id\":1001}}", text1);
    }

    public void test_dupType2() throws Exception {
        WriteDuplicateType.DianDianCart cart = new WriteDuplicateType.DianDianCart();
        cart.setId(1001);
        LinkedHashMap<String, HashMap<String, Object>> cartMap = new LinkedHashMap<String, HashMap<String, Object>>();
        HashMap<String, Object> obj = new HashMap<String, Object>();
        obj.put("id", 1001);
        obj.put(DEFAULT_TYPE_KEY, "com.alibaba.json.bvt.writeClassName.WriteDuplicateType$DianDianCart");
        cartMap.put("1001", obj);
        String text1 = JSON.toJSONString(cartMap, WriteClassName);
        Assert.assertEquals("{\"@type\":\"java.util.LinkedHashMap\",\"1001\":{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteDuplicateType$DianDianCart\",\"id\":1001}}", text1);
    }

    public void test_dupType3() throws Exception {
        WriteDuplicateType.DianDianCart cart = new WriteDuplicateType.DianDianCart();
        cart.setId(1001);
        LinkedHashMap<String, LinkedHashMap<String, Object>> cartMap = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
        LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
        obj.put(DEFAULT_TYPE_KEY, "com.alibaba.json.bvt.writeClassName.WriteDuplicateType$DianDianCart");
        obj.put("id", 1001);
        cartMap.put("1001", obj);
        String text1 = JSON.toJSONString(cartMap, WriteClassName);
        Assert.assertEquals("{\"@type\":\"java.util.LinkedHashMap\",\"1001\":{\"@type\":\"com.alibaba.json.bvt.writeClassName.WriteDuplicateType$DianDianCart\",\"id\":1001}}", text1);
    }

    public static class DianDianCart {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

