package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_NonStringKeyMap extends TestCase {
    public void test_bug() throws Exception {
        Bug_for_NonStringKeyMap.VO vo = new Bug_for_NonStringKeyMap.VO();
        vo.getMap().put(1L, new Bug_for_NonStringKeyMap.VAL());
        String text = JSON.toJSONString(vo, WriteClassName);
        System.out.println(text);
        JSON.parse(text);
    }

    public void test_1() throws Exception {
        Map<Map<String, String>, String> map = new HashMap<Map<String, String>, String>();
        Map<String, String> submap = new HashMap<String, String>();
        submap.put("subkey", "subvalue");
        map.put(submap, "value");
        String jsonString = JSON.toJSONString(map, WriteClassName);
        System.out.println(jsonString);
        Object object = JSON.parse(jsonString);
        System.out.println(object.toString());
    }

    public static class VO {
        private Map<Long, Bug_for_NonStringKeyMap.VAL> map = new HashMap<Long, Bug_for_NonStringKeyMap.VAL>();

        public Map<Long, Bug_for_NonStringKeyMap.VAL> getMap() {
            return map;
        }

        public void setMap(Map<Long, Bug_for_NonStringKeyMap.VAL> map) {
            this.map = map;
        }
    }

    public static class VAL {}
}

