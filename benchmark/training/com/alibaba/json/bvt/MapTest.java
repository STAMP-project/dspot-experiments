package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class MapTest extends TestCase {
    public void test_null() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(null, "123");
        String text = JSON.toJSONString(map);
        Assert.assertEquals("{null:\"123\"}", text);
    }
}

