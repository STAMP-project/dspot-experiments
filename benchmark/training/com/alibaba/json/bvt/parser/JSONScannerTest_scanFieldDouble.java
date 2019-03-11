package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONScannerTest_scanFieldDouble extends TestCase {
    public void test_0() throws Exception {
        String text = "{\"value\":1.0}";
        JSONScannerTest_scanFieldDouble.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        Assert.assertTrue((1.0 == (obj.getValue())));
    }

    public void test_1() throws Exception {
        String text = "{\"value\":\"1\"}";
        JSONScannerTest_scanFieldDouble.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        Assert.assertTrue((1.0 == (obj.getValue())));
    }

    public void test_2() throws Exception {
        String text = "{\"f1\":2,\"value\":1.0}";
        JSONScannerTest_scanFieldDouble.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        Assert.assertTrue((1.0 == (obj.getValue())));
    }

    public void test_3() throws Exception {
        String text = "{\"value\":1.01}";
        JSONScannerTest_scanFieldDouble.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        Assert.assertTrue((1.01 == (obj.getValue())));
    }

    public void test_4() throws Exception {
        String text = "{\"value\":1.}";
        JSONScannerTest_scanFieldDouble.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        Assert.assertTrue((1.0 == (obj.getValue())));
    }

    public void test_5() throws Exception {
        String text = "{\"value\":922337203685477580723}";
        JSONScannerTest_scanFieldDouble.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        Assert.assertTrue((9.223372036854776E20 == (obj.getValue())));
    }

    public void test_error_2() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":32K}";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":32}{";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_4() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":\u4e2d}";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_5() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":3.F";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_6() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":3.2]";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_7() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":3.2}]";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_8() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":3.2}}";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_9() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":3.2},";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_10() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":3.\\0}";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_11() throws Exception {
        JSONException error = null;
        try {
            String text = "{\"value\":3.\u4e2d}";
            JSON.parseObject(text, JSONScannerTest_scanFieldDouble.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        private double value;

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}

