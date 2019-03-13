package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ValueFilter;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldsTest7 extends TestCase {
    public void test_enum() throws Exception {
        EnumFieldsTest7.Model model = new EnumFieldsTest7.Model();
        model.t1 = EnumFieldsTest7.Type.A;
        model.t2 = null;
        ValueFilter filter = new ValueFilter() {
            public Object process(Object object, String name, Object value) {
                return null;
            }
        };
        String text = JSON.toJSONString(model, filter, WriteMapNullValue);
        Assert.assertEquals("{\"t1\":null,\"t2\":null}", text);
    }

    public static class Model {
        private EnumFieldsTest7.Type t1;

        private EnumFieldsTest7.Type t2;

        public EnumFieldsTest7.Type getT1() {
            return t1;
        }

        public void setT1(EnumFieldsTest7.Type t1) {
            this.t1 = t1;
        }

        public EnumFieldsTest7.Type getT2() {
            return t2;
        }

        public void setT2(EnumFieldsTest7.Type t2) {
            this.t2 = t2;
        }
    }

    private static enum Type {

        A,
        B,
        C;}
}

