package com.alibaba.json.bvt.serializer.enum_;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class EnumFieldsTest2 extends TestCase {
    public void test_enum() throws Exception {
        EnumFieldsTest2.Model model = new EnumFieldsTest2.Model();
        model.t1 = EnumFieldsTest2.Type.A;
        model.t2 = null;
        String text = JSON.toJSONString(model, WriteMapNullValue);
        Assert.assertEquals("{\"t1\":\"A\",\"t2\":null}", text);
    }

    public static class Model {
        private EnumFieldsTest2.Type t1;

        private EnumFieldsTest2.Type t2;

        public EnumFieldsTest2.Type getT1() {
            return t1;
        }

        public void setT1(EnumFieldsTest2.Type t1) {
            this.t1 = t1;
        }

        public EnumFieldsTest2.Type getT2() {
            return t2;
        }

        public void setT2(EnumFieldsTest2.Type t2) {
            this.t2 = t2;
        }
    }

    private static enum Type {

        A,
        B,
        C;}
}

