package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class Issue94 extends TestCase {
    public void test_for_issue() throws Exception {
        JSONObject o = new JSONObject();
        o.put("line", "{\"1\":\u0080}");
        o.toString();
    }
}

