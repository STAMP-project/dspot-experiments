package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ThrowableDeserializerTest_2 extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals("xxx", JSON.parseObject("{\"message\":\"xxx\",,,}", ThrowableDeserializerTest_2.MyException.class).getMessage());
    }

    public void test_2() throws Exception {
        Assert.assertEquals(null, JSON.parseObject("{\"message\":\"xxx\"}", ThrowableDeserializerTest_2.MyException2.class).getMessage());
    }

    public void test_3() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject(",\"message\":\"xxx\"}", ThrowableDeserializerTest_2.MyException.class);
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public static class MyException extends Exception {
        public MyException() {
        }

        public MyException(String message) {
            super(message);
        }
    }

    public static class MyException2 extends Exception {
        public MyException2() {
        }
    }
}

