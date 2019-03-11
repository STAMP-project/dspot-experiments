package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import java.io.StringReader;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldTest extends TestCase {
    public void test_special() throws Exception {
        JSONReader read = new JSONReader(new StringReader("{\"value\":1}"));
        EnumFieldTest.Model model = read.readObject(EnumFieldTest.Model.class);
        Assert.assertEquals(EnumFieldTest.Type.B, model.value);
        read.close();
    }

    public void test_1() throws Exception {
        JSONReader read = new JSONReader(new StringReader("{\"value\":\"A\",\"value1\":\"B\"}"));
        EnumFieldTest.Model model = read.readObject(EnumFieldTest.Model.class);
        Assert.assertEquals(EnumFieldTest.Type.A, model.value);
        Assert.assertEquals(EnumFieldTest.Type.B, model.value1);
        read.close();
    }

    public void test_map() throws Exception {
        JSONReader read = new JSONReader(new StringReader("{\"model\":{\"value\":\"A\",\"value1\":\"B\"}}"));
        Map<String, EnumFieldTest.Model> map = read.readObject(new com.alibaba.fastjson.TypeReference<Map<String, EnumFieldTest.Model>>() {});
        EnumFieldTest.Model model = ((EnumFieldTest.Model) (map.get("model")));
        Assert.assertEquals(EnumFieldTest.Type.A, model.value);
        Assert.assertEquals(EnumFieldTest.Type.B, model.value1);
        read.close();
    }

    public void test_error() throws Exception {
        JSONReader read = new JSONReader(new StringReader("{\"value\":\"a\\b\"}"));
        EnumFieldTest.Model model = read.readObject(EnumFieldTest.Model.class);
        TestCase.assertNull(model.value);
    }

    public void test_error_1() throws Exception {
        Exception error = null;
        try {
            JSONReader read = new JSONReader(new StringReader("{\"value\":\"A\",\"value1\":\"B\"["));
            EnumFieldTest.Model model = read.readObject(EnumFieldTest.Model.class);
            read.readObject(EnumFieldTest.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    public void test_error_2() throws Exception {
        Exception error = null;
        try {
            JSONReader read = new JSONReader(new StringReader("{\"model\":{\"value\":\"A\",\"value1\":\"B\"}["));
            Map<String, EnumFieldTest.Model> map = read.readObject(new com.alibaba.fastjson.TypeReference<Map<String, EnumFieldTest.Model>>() {});
            read.readObject(EnumFieldTest.Model.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    private static class Model {
        public EnumFieldTest.Type value;

        public EnumFieldTest.Type value1;
    }

    public static enum Type {

        A,
        B,
        C;}
}

