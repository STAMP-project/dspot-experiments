package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldsTest5 extends TestCase {
    public void test_enum() throws Exception {
        EnumFieldsTest5.Model model = new EnumFieldsTest5.Model();
        model.types.add(EnumFieldsTest5.Type.A);
        model.types.add(null);
        String text = JSON.toJSONString(model, WriteMapNullValue);
        Assert.assertEquals("{\"types\":[\"A\",null]}", text);
    }

    public static class Model {
        public Collection<EnumFieldsTest5.Type> types = Collections.synchronizedCollection(new ArrayList<EnumFieldsTest5.Type>());
    }

    private static enum Type {

        A,
        B,
        C;}
}

