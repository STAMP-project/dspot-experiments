package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldsTest4 extends TestCase {
    public void test_enum() throws Exception {
        EnumFieldsTest4.Model model = new EnumFieldsTest4.Model();
        model.types = new EnumFieldsTest4.Type[]{ EnumFieldsTest4.Type.A, null };
        String text = JSON.toJSONString(model, WriteMapNullValue);
        Assert.assertEquals("{\"types\":[\"A\",null]}", text);
    }

    public static class Model {
        public EnumFieldsTest4.Type[] types;
    }

    private static enum Type {

        A,
        B,
        C;}
}

