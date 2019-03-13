package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONScannerTest_scanFieldString extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"value\":1}";
        JSONScannerTest_scanFieldString.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldString.VO.class);
        Assert.assertEquals("1", obj.getValue());
    }

    public void test_1() throws Exception {
        String text = "{\"value\":\"1\"}";
        JSONScannerTest_scanFieldString.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldString.VO.class);
        Assert.assertEquals("1", obj.getValue());
    }

    public void test_2() throws Exception {
        String text = "{\"value\":\"1\\t\"}";
        JSONScannerTest_scanFieldString.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldString.VO.class);
        Assert.assertEquals("1\t", obj.getValue());
    }

    public void test_3() throws Exception {
        String text = "{\"value\":\"1\\n\"}";
        JSONScannerTest_scanFieldString.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldString.VO.class);
        Assert.assertEquals("1\n", obj.getValue());
    }

    public void test_error_0() {
        Exception error = null;
        try {
            String text = "{\"value\":\"1\\n\"";
            JSON.parseObject(text, JSONScannerTest_scanFieldString.VO.class);
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

