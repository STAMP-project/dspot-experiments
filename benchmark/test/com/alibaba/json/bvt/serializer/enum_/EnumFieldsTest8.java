package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.BrowserCompatible;
import SerializerFeature.QuoteFieldNames;
import SerializerFeature.WriteEnumUsingName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.ValueFilter;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldsTest8 extends TestCase {
    public void test_enum() throws Exception {
        EnumFieldsTest8.Model model = new EnumFieldsTest8.Model();
        model.t1 = EnumFieldsTest8.Type.A;
        model.t2 = null;
        ValueFilter valueFilter = new ValueFilter() {
            public Object process(Object object, String name, Object value) {
                return value;
            }
        };
        SerializeFilter[] filters = new SerializeFilter[]{ valueFilter };
        String text = // 
        // 
        // 
        // 
        JSON.toJSONString(model, SerializeConfig.getGlobalInstance(), filters, null, 0, QuoteFieldNames, BrowserCompatible, WriteEnumUsingName);
        Assert.assertEquals("{\"t1\":\"A\"}", text);
    }

    public static class Model {
        public EnumFieldsTest8.Type t1;

        public EnumFieldsTest8.Type t2;
    }

    public static enum Type {

        A,
        B,
        C;}
}

