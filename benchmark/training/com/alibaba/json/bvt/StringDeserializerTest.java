package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringDeserializerTest extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals("123", JSON.parseObject("123", String.class));
        Assert.assertEquals("true", JSON.parseObject("true", String.class));
        Assert.assertEquals(null, JSON.parseObject("null", String.class));
    }

    public void test_StringBuffer() throws Exception {
        Assert.assertTrue(equals(new StringBuffer("123"), JSON.parseObject("123", StringBuffer.class)));
        Assert.assertTrue(equals(new StringBuffer("true"), JSON.parseObject("true", StringBuffer.class)));
        Assert.assertEquals(null, JSON.parseObject("null", StringBuffer.class));
    }

    public void test_StringBuilder() throws Exception {
        Assert.assertTrue(equals(new StringBuilder("123"), JSON.parseObject("123", StringBuilder.class)));
        Assert.assertTrue(equals(new StringBuilder("true"), JSON.parseObject("true", StringBuilder.class)));
        Assert.assertEquals(null, JSON.parseObject("null", StringBuilder.class));
    }
}

