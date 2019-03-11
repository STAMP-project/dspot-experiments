package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class Bug_for_typeReference extends TestCase {
    public void test_0() throws Exception {
        String text = "[]";
        JSON.parseObject(text, getType());
    }
}

