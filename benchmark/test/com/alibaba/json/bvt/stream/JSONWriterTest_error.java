package com.alibaba.json.bvt.stream;


import SerializerFeature.UseSingleQuotes;
import com.alibaba.fastjson.JSONWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONWriterTest_error extends TestCase {
    public void test_writer() throws Exception {
        Field field = JSONWriter.class.getDeclaredField("context");
        field.setAccessible(true);
        StringWriter out = new StringWriter();
        JSONWriter writer = new JSONWriter(out);
        writer.config(UseSingleQuotes, true);
        writer.startObject();
        Object context = field.get(writer);
        Field stateField = context.getClass().getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(context, (-1));
        Exception error = null;
        try {
            writer.startObject();
        } catch (Exception ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        writer.close();
    }
}

