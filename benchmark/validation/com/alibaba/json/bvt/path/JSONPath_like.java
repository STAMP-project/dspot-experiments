package com.alibaba.json.bvt.path;


import com.alibaba.fastjson.JSONPath;
import junit.framework.TestCase;


public class JSONPath_like extends TestCase {
    public void test_like_not_match() throws Exception {
        TestCase.assertNull(JSONPath.read("{\"table\":\"_order_base\"}", "[table LIKE 'order_base%']"));
    }

    public void test_like_not_match_1() throws Exception {
        TestCase.assertEquals("{\"table\":\"_order_base\"}", JSONPath.read("{\"table\":\"_order_base\"}", "[table LIKE '_order_base%']").toString());
    }
}

