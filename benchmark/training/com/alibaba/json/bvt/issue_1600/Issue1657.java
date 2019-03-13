package com.alibaba.json.bvt.issue_1600;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import junit.framework.TestCase;


public class Issue1657 extends TestCase {
    public void test_for_issue() throws Exception {
        HashMap map = JSON.parseObject("\"\"", HashMap.class);
        TestCase.assertEquals(0, map.size());
    }
}

