package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class MapDeserializerTest extends TestCase {
    public void test_0() throws Exception {
        JSON.parseObject("{\"@type\":\"com.alibaba.json.bvt.parser.deser.MapDeserializerTest$MyMap\"}", Map.class);
    }

    public static class MyMap extends HashMap {
        public MyMap() {
        }
    }
}

