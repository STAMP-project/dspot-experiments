package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Issue118 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = JSON.toJSONString("\u0000");
        TestCase.assertEquals("\"\\u0000\"", json);
    }
}

