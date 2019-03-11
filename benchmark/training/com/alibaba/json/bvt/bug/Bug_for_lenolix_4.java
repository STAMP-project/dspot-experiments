package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_lenolix_4 extends TestCase {
    public void test_for_objectKey() throws Exception {
        Map<Map<String, String>, String> map = new HashMap<Map<String, String>, String>();
        Map<String, String> submap = new HashMap<String, String>();
        submap.put("subkey", "subvalue");
        map.put(submap, "value");
        String jsonString = JSON.toJSONString(map, WriteClassName);
        System.out.println(jsonString);
        Object object = JSON.parse(jsonString);
        JSON.parseObject(jsonString);
        System.out.println(object.toString());
    }

    public void test_for_arrayKey() throws Exception {
        Map<List<String>, String> map = new HashMap<List<String>, String>();
        List<String> key = new ArrayList<String>();
        key.add("subkey");
        map.put(key, "value");
        String jsonString = JSON.toJSONString(map, WriteClassName);
        System.out.println(jsonString);
        Object object = JSON.parse(jsonString);
        System.out.println(object.toString());
    }
}

