package com.alibaba.json.bvt.basicType;


import SerializerFeature.BrowserCompatible;
import com.alibaba.fastjson.JSON;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;


public class BigDecimal_BrowserCompatible extends TestCase {
    public void test_for_issue() throws Exception {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("id1", new BigDecimal("-9223370018640066466"));
        map.put("id2", new BigDecimal("9223370018640066466"));
        map.put("id3", new BigDecimal("100"));
        TestCase.assertEquals("{\"id1\":\"-9223370018640066466\",\"id2\":\"9223370018640066466\",\"id3\":100}", JSON.toJSONString(map, BrowserCompatible));
    }
}

