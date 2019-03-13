package com.alibaba.json.bvt.serializer.stream;


import com.alibaba.fastjson.serializer.SerializeWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class StreamWriterTest_writeArray extends TestCase {
    public void test_0() throws Exception {
        StringWriter out = new StringWriter();
        SerializeWriter writer = new SerializeWriter(out, 10);
        Assert.assertEquals(10, writer.getBufferLength());
        for (char ch = 'a'; ch <= 'z'; ++ch) {
            char[] chars = new char[]{ ch, ch, ch };
            writer.write(chars, 0, chars.length);
        }
        writer.close();
        String text = out.toString();
        Assert.assertEquals((26 * 3), text.length());
        for (int i = 0; i < 26; ++i) {
            Assert.assertEquals(text.charAt((i * 3)), ((char) ('a' + i)));
            Assert.assertEquals(text.charAt(((i * 3) + 1)), ((char) ('a' + i)));
            Assert.assertEquals(text.charAt(((i * 3) + 2)), ((char) ('a' + i)));
        }
    }
}

