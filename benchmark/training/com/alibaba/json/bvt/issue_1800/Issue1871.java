package com.alibaba.json.bvt.issue_1800;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1871 extends TestCase {
    public void test_for_issue() throws Exception {
        Issue1871.UnwrapClass m = new Issue1871.UnwrapClass();
        m.map = new HashMap();
        m.name = "ljw";
        m.map.put("a", "1");
        m.map.put("b", "2");
        String json = JSON.toJSONString(m, WriteClassName);
        System.out.println(json);
    }

    public static class UnwrapClass {
        private String name;

        @JSONField(unwrapped = true)
        private Map<String, String> map;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }
    }
}

