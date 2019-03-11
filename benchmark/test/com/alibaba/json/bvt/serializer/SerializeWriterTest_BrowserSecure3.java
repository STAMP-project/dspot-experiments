package com.alibaba.json.bvt.serializer;


import SerializerFeature.BrowserSecure;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class SerializeWriterTest_BrowserSecure3 extends TestCase {
    public void test_0() throws Exception {
        String text = JSON.toJSONString("\n", BrowserSecure);
        Assert.assertEquals("\"\\n\"", text);
    }
}

