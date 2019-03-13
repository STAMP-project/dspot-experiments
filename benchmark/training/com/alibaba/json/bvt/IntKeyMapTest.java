package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class IntKeyMapTest extends TestCase {
    public void test_0() throws Exception {
        JSON.parse("{1:\"AA\",2:{}}");
    }
}

