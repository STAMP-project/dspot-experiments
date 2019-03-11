package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue171 extends TestCase {
    public void test_for_issue() throws Exception {
        Map m = new HashMap();
        m.put("a", "\u000b");
        String json = JSON.toJSONString(m);
        System.out.println(json);
        JSON.parse(json);
    }
}

