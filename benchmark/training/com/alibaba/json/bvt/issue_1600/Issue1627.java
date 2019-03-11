package com.alibaba.json.bvt.issue_1600;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


public class Issue1627 extends TestCase {
    public void test_for_issue() throws Exception {
        String a = "{\"101a0.test-b\":\"tt\"}";
        Object o = JSON.parse(a);
        String s = "101a0.test-b";
        TestCase.assertTrue(JSONPath.contains(o, ("$." + (Issue1627.escapeString(s)))));
    }
}

