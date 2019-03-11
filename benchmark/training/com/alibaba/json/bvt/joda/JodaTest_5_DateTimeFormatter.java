package com.alibaba.json.bvt.joda;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class JodaTest_5_DateTimeFormatter extends TestCase {
    public void test_for_joda_0() throws Exception {
        String json = "{\"formatter\":\"yyyyMMdd\"}";
        JodaTest_5_DateTimeFormatter.Model m = JSON.parseObject(json, JodaTest_5_DateTimeFormatter.Model.class);
        TestCase.assertEquals(DateTimeFormat.forPattern("yyyyMMdd"), m.formatter);
    }

    public static class Model {
        public DateTimeFormatter formatter;
    }
}

