package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import java.util.List;
import junit.framework.TestCase;


public class TestException extends TestCase {
    public void test_0() throws Exception {
        Exception error = null;
        try {
            f();
        } catch (Exception ex) {
            error = ex;
        }
        String text = JSON.toJSONString(new Exception[]{ error });
        List<RuntimeException> list = JSON.parseArray(text, RuntimeException.class);
        JSON.toJSONString(list);
    }
}

