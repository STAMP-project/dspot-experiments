package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class FloatArrayFieldTest_primitive extends TestCase {
    public void test_codec_null() throws Exception {
        FloatArrayFieldTest_primitive.V0 v = new FloatArrayFieldTest_primitive.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        FloatArrayFieldTest_primitive.V0 v1 = JSON.parseObject(text, FloatArrayFieldTest_primitive.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        FloatArrayFieldTest_primitive.V0 v = new FloatArrayFieldTest_primitive.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty);
        Assert.assertEquals("{\"value\":[]}", text);
    }

    public static class V0 {
        private float[] value;

        public float[] getValue() {
            return value;
        }

        public void setValue(float[] value) {
            this.value = value;
        }
    }
}

