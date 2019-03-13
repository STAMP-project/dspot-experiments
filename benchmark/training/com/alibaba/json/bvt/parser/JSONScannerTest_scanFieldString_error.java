package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONScannerTest_scanFieldString_error extends TestCase {
    public void test_error_2() {
        Exception error = null;
        try {
            String text = "{\"value\":\"1\"}1";
            JSON.parseObject(text, JSONScannerTest_scanFieldString_error.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() {
        Exception error = null;
        try {
            String text = "{\"value\":\"1\"1";
            JSON.parseObject(text, JSONScannerTest_scanFieldString_error.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

