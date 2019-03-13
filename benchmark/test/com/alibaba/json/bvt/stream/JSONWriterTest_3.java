package com.alibaba.json.bvt.stream;


import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.JSONWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONWriterTest_3 extends TestCase {
    public void test_writer() throws Exception {
        StringWriter out = new StringWriter();
        JSONWriter writer = new JSONWriter(out);
        writer.config(UseSingleQuotes, true);
        writer.startObject();
        writer.startObject();
        writer.endObject();
        writer.startObject();
        writer.endObject();
        writer.endObject();
        writer.close();
        Assert.assertEquals("{{}:{}}", out.toString());
    }
}

