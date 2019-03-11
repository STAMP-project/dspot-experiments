package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldsTest3 extends TestCase {
    public void test_enum() throws Exception {
        EnumFieldsTest3.Model model = new EnumFieldsTest3.Model();
        model.types.add(EnumFieldsTest3.Type.A);
        model.types.add(null);
        String text = JSON.toJSONString(model, WriteMapNullValue);
        Assert.assertEquals("{\"types\":[\"A\",null]}", text);
    }

    public static class Model {
        public List<EnumFieldsTest3.Type> types = new ArrayList<EnumFieldsTest3.Type>();
    }

    private static enum Type {

        A,
        B,
        C;}
}

