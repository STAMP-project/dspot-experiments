package com.alibaba.json.bvt.stream;


import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.JSONWriter;
import java.io.StringWriter;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONWriterTest_4 extends TestCase {
    public void test_writer() throws Exception {
        StringWriter out = new StringWriter();
        JSONWriter writer = new JSONWriter(out);
        writer.config(UseSingleQuotes, true);
        writer.writeObject(Collections.emptyMap());
        writer.close();
        Assert.assertEquals("{}", out.toString());
    }
}

