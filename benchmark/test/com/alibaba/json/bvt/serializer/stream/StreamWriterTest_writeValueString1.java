package com.alibaba.json.bvt.serializer.stream;


import SerializerFeature.BrowserCompatible;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class StreamWriterTest_writeValueString1 extends TestCase {
    public void test_0() throws Exception {
        StringWriter out = new StringWriter();
        SerializeWriter writer = new SerializeWriter(out, 10);
        writer.config(BrowserCompatible, true);
        Assert.assertEquals(10, writer.getBufferLength());
        writer.writeString("abcde12345678\t");
        writer.close();
        String text = out.toString();
        Assert.assertEquals("\"abcde12345678\\t\"", text);
    }
}

