package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldsTest extends TestCase {
    public void test_enum() throws Exception {
        EnumFieldsTest.Model model = new EnumFieldsTest.Model();
        model.t1 = EnumFieldsTest.Type.A;
        model.t2 = null;
        String text = JSON.toJSONString(model, WriteMapNullValue);
        Assert.assertEquals("{\"t1\":\"A\",\"t2\":null}", text);
    }

    public static class Model {
        public EnumFieldsTest.Type t1;

        public EnumFieldsTest.Type t2;
    }

    private static enum Type {

        A,
        B,
        C;}
}

