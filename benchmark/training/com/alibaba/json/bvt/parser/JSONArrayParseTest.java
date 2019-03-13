package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONArrayParseTest extends TestCase {
    public void test_array() throws Exception {
        String text = "[{id:123}]";
        List<Map<String, Integer>> array = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<List<Map<String, Integer>>>() {});
        Assert.assertEquals(1, array.size());
        Map<String, Integer> map = array.get(0);
        Assert.assertEquals(123, map.get("id").intValue());
    }
}

