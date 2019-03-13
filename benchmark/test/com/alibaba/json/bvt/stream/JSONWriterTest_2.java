package com.alibaba.json.bvt.stream;


import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.JSONWriter;
import java.io.StringWriter;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONWriterTest_2 extends TestCase {
    public void test_writer() throws Exception {
        StringWriter out = new StringWriter();
        JSONWriter writer = new JSONWriter(out);
        writer.config(UseSingleQuotes, true);
        writer.startObject();
        writer.writeObject("a");
        writer.writeObject("1");
        writer.writeObject("b");
        writer.writeObject("2");
        writer.writeObject("c");
        writer.writeObject("3");
        writer.endObject();
        writer.close();
        Assert.assertEquals("{'a':'1','b':'2','c':'3'}", out.toString());
    }
}

