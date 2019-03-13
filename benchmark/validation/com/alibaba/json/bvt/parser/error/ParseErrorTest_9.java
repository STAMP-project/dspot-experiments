package com.alibaba.json.bvt.parser.error;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import junit.framework.TestCase;
import org.junit.Assert;


public class ParseErrorTest_9 extends TestCase {
    public void test_for_error() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":33\"}", ParseErrorTest_9.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_for_error_2() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":\"33\"", ParseErrorTest_9.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_for_error_3() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":\"33\",", ParseErrorTest_9.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_for_error_4() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"value\":\"33\"},", ParseErrorTest_9.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class Model {
        public long value;
    }
}

