package com.alibaba.json.bvt.stream;


import SerializerFeature.WriteNullStringAsEmpty;
import com.alibaba.fastjson.JSONWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONWriterTest_6 extends TestCase {
    public void test_writer() throws Exception {
        StringWriter out = new StringWriter();
        JSONWriter writer = new JSONWriter(out);
        writer.config(WriteNullStringAsEmpty, true);
        writer.startObject();
        writer.writeKey("value");
        writer.writeObject(((String) (null)));
        writer.endObject();
        writer.close();
        Assert.assertEquals("{\"value\":\"\"}", out.toString());
    }
}

