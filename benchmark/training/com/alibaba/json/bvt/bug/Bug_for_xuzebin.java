package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import junit.framework.TestCase;


public class Bug_for_xuzebin extends TestCase {
    public void testMap() {
        Bug_for_xuzebin.P p = new Bug_for_xuzebin.P();
        p.setI(2);
        p.getMap().put("a", "b");
        String json = JSON.toJSONString(p, WriteClassName);
        System.out.println(json);
        Bug_for_xuzebin.P x = JSON.parseObject(json, Bug_for_xuzebin.P.class);
        System.out.println(JSON.toJSONString(x));
    }

    public void testMap2() {
        Bug_for_xuzebin.P p = new Bug_for_xuzebin.P();
        p.setI(2);
        // p.getMap().put("a", "b");
        String json = JSON.toJSONString(p, WriteClassName);
        System.out.println(json);
        Bug_for_xuzebin.P x = JSON.parseObject(json, Bug_for_xuzebin.P.class);
        System.out.println(JSON.toJSONString(x));
    }

    public static class P {
        private Map<String, String> map = new ConcurrentHashMap<String, String>();

        private int i = 0;

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }
}

