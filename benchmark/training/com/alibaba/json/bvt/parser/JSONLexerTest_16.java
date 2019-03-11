package com.alibaba.json.bvt.parser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONLexerTest_16 extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\0\":123}").get("\u0000"));
    }

    public void test_1() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\1\":123}").get("\u0001"));
    }

    public void test_2() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\2\":123}").get("\u0002"));
    }

    public void test_3() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\3\":123}").get("\u0003"));
    }

    public void test_4() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\4\":123}").get("\u0004"));
    }

    public void test_5() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\5\":123}").get("\u0005"));
    }

    public void test_6() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\6\":123}").get("\u0006"));
    }

    public void test_7() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\7\":123}").get("\u0007"));
    }

    public void test_8() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\b\":123}").get("\b"));
    }

    public void test_9() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\t\":123}").get("\t"));
    }

    public void test_10() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\n\":123}").get("\n"));
    }

    public void test_39() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\\'\":123}").get("\'"));
    }

    public void test_40() throws Exception {
        Assert.assertEquals(123, JSON.parseObject("{\"\\xFF\":123}").get("\u00ff"));
    }
}

