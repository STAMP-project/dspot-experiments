package com.alibaba.json.bvt.parser.number;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class NumberValueTest_error_7 extends TestCase {
    public void test_0() throws Exception {
        Exception error = null;
        try {
            String text = "{\"value\":-";
            JSON.parse(text);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
}

