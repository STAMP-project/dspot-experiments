package com.alibaba.json.bvt.serializer.features;


import SerializerFeature.WriteNonStringValueAsString;
import com.alibaba.fastjson.JSON;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteNonStringValueAsStringTestMap extends TestCase {
    public void test_0() throws Exception {
        Map map = new LinkedHashMap();
        map.put("key1", new Float(100));
        map.put("key2", 100);
        map.put("key3", Boolean.TRUE);
        map.put("key4", true);
        map.put(1, 200);
        map.put(new Object(), 100);
        String text = JSON.toJSONString(map, WriteNonStringValueAsString);
        Assert.assertEquals("{\"key1\":\"100.0\",\"key2\":\"100\",\"key3\":\"true\",\"key4\":\"true\",1:\"200\",{}:\"100\"}", text);
    }
}

