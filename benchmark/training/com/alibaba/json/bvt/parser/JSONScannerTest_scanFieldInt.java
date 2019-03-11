package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONScannerTest_scanFieldInt extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"value\":1.0}";
        JSONScannerTest_scanFieldInt.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldInt.VO.class);
        Assert.assertEquals(1, obj.getValue());
    }

    public void test_1() throws Exception {
        String text = "{\"value\":\"1\"}";
        JSONScannerTest_scanFieldInt.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldInt.VO.class);
        Assert.assertEquals(1, obj.getValue());
    }

    public void test_error_1() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":922337203685477580723}";
            JSON.parseObject(text, JSONScannerTest_scanFieldInt.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":32O}";
            JSON.parseObject(text, JSONScannerTest_scanFieldInt.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":32}{";
            JSON.parseObject(text, JSONScannerTest_scanFieldInt.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_4() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":\u4e2d}";
            JSON.parseObject(text, JSONScannerTest_scanFieldInt.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_5() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":\u0000}";
            JSON.parseObject(text, JSONScannerTest_scanFieldInt.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

