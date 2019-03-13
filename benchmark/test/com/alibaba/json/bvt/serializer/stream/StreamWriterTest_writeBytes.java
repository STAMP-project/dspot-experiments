package com.alibaba.json.bvt.serializer.stream;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class StreamWriterTest_writeBytes extends TestCase {
    public void test_0() throws Exception {
        StringWriter out = new StringWriter();
        byte[] bytes = "??".getBytes("GB2312");
        SerializeWriter writer = new SerializeWriter(out, 10);
        Assert.assertEquals(10, writer.getBufferLength());
        writer.writeByteArray(bytes);
        writer.close();
        String text = out.toString();
        byte[] result = JSON.parseObject(text, byte[].class);
        Assert.assertEquals("??", new String(result, "GB2312"));
    }
}

