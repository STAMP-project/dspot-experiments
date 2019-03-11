package com.alibaba.json.bvt.serializer;


import com.alibaba.fastjson.JSON;
import java.util.Collections;
import junit.framework.TestCase;


public class UnicodeTest extends TestCase {
    public void test_unicode() throws Exception {
        String text = JSON.toJSONString(Collections.singletonMap("v", "\u0018"));
        System.out.println(text);
    }
}

