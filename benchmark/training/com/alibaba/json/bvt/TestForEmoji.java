package com.alibaba.json.bvt;


import SerializerFeature.BrowserCompatible;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class TestForEmoji extends TestCase {
    public void test_0() throws Exception {
        Assert.assertEquals("\"\\uE507\"", JSON.toJSONString("\ue507", BrowserCompatible));
        Assert.assertEquals("\"\\uE501\"", JSON.toJSONString("\ue501", BrowserCompatible));
        Assert.assertEquals("\"\\uE44C\"", JSON.toJSONString("\ue44c", BrowserCompatible));
        Assert.assertEquals("\"\\uE401\"", JSON.toJSONString("\ue401", BrowserCompatible));
        Assert.assertEquals("\"\\uE253\"", JSON.toJSONString("\ue253", BrowserCompatible));
        Assert.assertEquals("\"\\uE201\"", JSON.toJSONString("\ue201", BrowserCompatible));
        Assert.assertEquals("\"\\uE15A\"", JSON.toJSONString("\ue15a", BrowserCompatible));
        Assert.assertEquals("\"\\uE101\"", JSON.toJSONString("\ue101", BrowserCompatible));
        Assert.assertEquals("\"\\uE05A\"", JSON.toJSONString("\ue05a", BrowserCompatible));
        Assert.assertEquals("\"\\uE001\"", JSON.toJSONString("\ue001", BrowserCompatible));
        // E507
    }

    public void test_zh() throws Exception {
        Assert.assertEquals("\"\\u4E2D\\u56FD\"", JSON.toJSONString("??", BrowserCompatible));
    }
}

