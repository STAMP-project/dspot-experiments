package com.alibaba.json.bvt.serializer.stream;


import SerializerFeature.QuoteFieldNames;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class StreamWriterTest_writeFieldValue_long extends TestCase {
    public void test_0() throws Exception {
        StringWriter out = new StringWriter();
        SerializeWriter writer = new SerializeWriter(out, 10);
        writer.config(QuoteFieldNames, true);
        Assert.assertEquals(10, writer.getBufferLength());
        writer.writeFieldValue(',', "abcde01245abcde", 123L);
        writer.close();
        String text = out.toString();
        Assert.assertEquals(",\"abcde01245abcde\":123", text);
    }
}

