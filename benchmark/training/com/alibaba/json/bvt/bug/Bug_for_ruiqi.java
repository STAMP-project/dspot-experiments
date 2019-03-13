package com.alibaba.json.bvt.bug;


import SerializerFeature.WriteEnumUsingToString;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_ruiqi extends TestCase {
    public void test_0() throws Exception {
        Map<String, Bug_for_ruiqi.Enum> map = new HashMap<String, Bug_for_ruiqi.Enum>();
        map.put("a", Bug_for_ruiqi.Enum.ENUM1);
        map.put("b", Bug_for_ruiqi.Enum.ENUM1);
        System.out.println(JSON.toJSONString(map, WriteEnumUsingToString));
        System.out.println(JSON.toJSONString(map));
    }

    public static enum Enum {

        ENUM1("name1"),
        ENUM2("name2");
        private String name;

        Enum(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "name: " + (name);
        }
    }
}

