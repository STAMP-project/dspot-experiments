package com.alibaba.json.bvt.parser.stream;


import Feature.AllowArbitraryCommas;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderTest_error extends TestCase {
    public void test_read() throws Exception {
        Field field = JSONReader.class.getDeclaredField("context");
        field.setAccessible(true);
        JSONReader reader = new JSONReader(new StringReader("[{}]"));
        reader.config(AllowArbitraryCommas, true);
        reader.startArray();
        Object context = field.get(reader);
        Field stateField = context.getClass().getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(context, (-1));
        {
            Exception error = null;
            try {
                reader.startObject();
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                reader.readInteger();
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }
}

