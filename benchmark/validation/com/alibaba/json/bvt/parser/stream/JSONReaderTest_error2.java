package com.alibaba.json.bvt.parser.stream;


import Feature.AllowArbitraryCommas;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import java.lang.reflect.Field;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONReaderTest_error2 extends TestCase {
    private static Object context;

    private static Field stateField;

    public void test_read() throws Exception {
        Field field = JSONReader.class.getDeclaredField("context");
        field.setAccessible(true);
        JSONReader reader = new JSONReader(new StringReader("[{}]"));
        reader.config(AllowArbitraryCommas, true);
        reader.startArray();
        JSONReaderTest_error2.context = field.get(reader);
        JSONReaderTest_error2.stateField = JSONReaderTest_error2.context.getClass().getDeclaredField("state");
        JSONReaderTest_error2.stateField.setAccessible(true);
        {
            Exception error = null;
            try {
                reader.readObject(JSONReaderTest_error2.VO.class);
            } catch (Exception ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
    }

    public static class VO {
        public VO() {
            try {
                JSONReaderTest_error2.stateField.set(JSONReaderTest_error2.context, (-1));
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

