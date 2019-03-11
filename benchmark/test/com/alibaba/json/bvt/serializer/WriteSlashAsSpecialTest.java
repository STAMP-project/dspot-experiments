package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteSlashAsSpecial;
import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class WriteSlashAsSpecialTest extends TestCase {
    public void test_0() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("value", "/");
        String result = JSON.toJSONString(map);
        Assert.assertEquals("{\"value\":\"/\"}", result);
    }

    public void test_1() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("value", "/");
        String result = JSON.toJSONString(map, WriteSlashAsSpecial);
        Assert.assertEquals("{\"value\":\"\\/\"}", result);
    }
}

