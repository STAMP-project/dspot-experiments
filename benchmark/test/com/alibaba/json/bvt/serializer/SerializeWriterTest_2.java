package com.alibaba.json.bvt.serializer;


import SerializerFeature.UseSingleQuotes;
import SerializerFeature.WriteTabAsSpecial;
import com.alibaba.fastjson.serializer.SerializeWriter;
import junit.framework.TestCase;
import org.junit.Assert;


@SuppressWarnings("deprecation")
public class SerializeWriterTest_2 extends TestCase {
    public void test_0() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(WriteTabAsSpecial, true);
        out.writeString("\t\n \b\n\r\f\\ \"");
        Assert.assertEquals("\"\\t\\n \\b\\n\\r\\f\\\\ \\\"\"", out.toString());
        out.close();
    }

    public void test_1() throws Exception {
        SerializeWriter out = new SerializeWriter(1);
        out.config(WriteTabAsSpecial, true);
        out.config(UseSingleQuotes, true);
        out.writeString("\t\n \b\n\r\f\\ \"");
        Assert.assertEquals("\'\\t\\n \\b\\n\\r\\f\\\\ \"\'", out.toString());
        out.close();
    }
}

