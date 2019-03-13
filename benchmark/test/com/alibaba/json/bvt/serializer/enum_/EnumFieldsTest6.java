package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldsTest6 extends TestCase {
    public void test_enum() throws Exception {
        EnumFieldsTest6.Model model = new EnumFieldsTest6.Model();
        model.types = new Object[]{ EnumFieldsTest6.Type.A, null };
        String text = JSON.toJSONString(model, WriteMapNullValue);
        Assert.assertEquals("{\"types\":[\"A\",null]}", text);
    }

    public static class Model {
        public Object[] types;
    }

    private static enum Type {

        A,
        B,
        C;}
}

