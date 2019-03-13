package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class UTF8ByteArrayLexerTest_symbol extends TestCase {
    public void test_utf8() throws Exception {
        byte[] bytes = "{\"name\":\"\u6e29\u5bb6\u5b9d\", \"name\":\"xx\"}".getBytes("UTF-8");
        JSONObject json = JSON.parseObject(bytes, JSONObject.class);
    }
}

