package com.alibaba.json.bvt.serializer.stream;


import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class StreamWriterTest_writeLong extends TestCase {
    public void test_0() throws Exception {
        StringWriter out = new StringWriter();
        SerializeWriter writer = new SerializeWriter(out, 10);
        Assert.assertEquals(10, writer.getBufferLength());
        writer.write("abcde");
        writer.writeLong(12345678L);
        writer.close();
        String text = out.toString();
        Assert.assertEquals("abcde12345678", text);
    }
}

