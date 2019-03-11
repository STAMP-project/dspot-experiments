package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONScannerTest_scanFieldBoolean extends TestCase {
    public void test_true() throws Exception {
        String text = "{\"value\":true}";
        JSONScannerTest_scanFieldBoolean.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        Assert.assertEquals(true, obj.getValue());
    }

    public void test_false() throws Exception {
        String text = "{\"value\":false}";
        JSONScannerTest_scanFieldBoolean.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        Assert.assertEquals(false, obj.getValue());
    }

    public void test_1() throws Exception {
        String text = "{\"value\":\"true\"}";
        JSONScannerTest_scanFieldBoolean.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        Assert.assertEquals(true, obj.getValue());
    }

    public void test_2() throws Exception {
        String text = "{\"value\":\"false\"}";
        JSONScannerTest_scanFieldBoolean.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        Assert.assertEquals(false, obj.getValue());
    }

    public void test_3() throws Exception {
        String text = "{\"value\":\"1\"}";
        JSONScannerTest_scanFieldBoolean.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        Assert.assertEquals(true, obj.getValue());
    }

    public void test_5() throws Exception {
        String text = "{\"value\":false}";
        JSONScannerTest_scanFieldBoolean.VO obj = JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        Assert.assertEquals(false, obj.getValue());
    }

    public void test_error_0() {
        Exception error = null;
        try {
            String text = "{\"value\":true\\n\"";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_1() {
        Exception error = null;
        try {
            String text = "{\"value\":a";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() {
        Exception error = null;
        try {
            String text = "{\"value\":teue}";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_3() {
        Exception error = null;
        try {
            String text = "{\"value\":tree}";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_4() {
        Exception error = null;
        try {
            String text = "{\"value\":truu}";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_5() {
        Exception error = null;
        try {
            String text = "{\"value\":fflse}";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_6() {
        Exception error = null;
        try {
            String text = "{\"value\":fasse}";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_7() {
        Exception error = null;
        try {
            String text = "{\"value\":falee}";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_8() {
        Exception error = null;
        try {
            String text = "{\"value\":falss}";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_9() {
        Exception error = null;
        try {
            String text = "{\"value\":false]";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_10() {
        Exception error = null;
        try {
            String text = "{\"value\":false}{";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_11() {
        Exception error = null;
        try {
            String text = "{\"value\":false}}";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_12() {
        Exception error = null;
        try {
            String text = "{\"value\":false}]";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_13() {
        Exception error = null;
        try {
            String text = "{\"value\":false},";
            JSON.parseObject(text, JSONScannerTest_scanFieldBoolean.VO.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class VO {
        private boolean value;

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }
}

