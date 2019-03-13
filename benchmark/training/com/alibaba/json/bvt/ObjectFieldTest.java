package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class ObjectFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        {
            ObjectFieldTest.V0 v = new ObjectFieldTest.V0();
            String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
            Assert.assertEquals("{\"value\":null}", text);
            ObjectFieldTest.V0 v1 = JSON.parseObject(text, ObjectFieldTest.V0.class);
            Assert.assertEquals(v1.getValue(), v.getValue());
        }
        {
            ObjectFieldTest.V0 v = new ObjectFieldTest.V0();
            v.setValue(Integer.valueOf(123));
            String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
            Assert.assertEquals("{\"value\":123}", text);
            ObjectFieldTest.V0 v1 = JSON.parseObject(text, ObjectFieldTest.V0.class);
            Assert.assertEquals(v1.getValue(), v.getValue());
        }
    }

    public void test_codec_null_1() throws Exception {
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        {
            ObjectFieldTest.V0 v = new ObjectFieldTest.V0();
            String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty);
            Assert.assertEquals("{\"value\":null}", text);
        }
        {
            ObjectFieldTest.V0 v = new ObjectFieldTest.V0();
            v.setValue(Integer.valueOf(123));
            String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
            Assert.assertEquals("{\"value\":123}", text);
            ObjectFieldTest.V0 v1 = JSON.parseObject(text, ObjectFieldTest.V0.class);
            Assert.assertEquals(v1.getValue(), v.getValue());
        }
    }

    public static class V0 {
        private Object value;

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}

