package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class DoubleArrayFieldTest_primitive extends TestCase {
    public void test_codec_null() throws Exception {
        DoubleArrayFieldTest_primitive.V0 v = new DoubleArrayFieldTest_primitive.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        DoubleArrayFieldTest_primitive.V0 v1 = JSON.parseObject(text, DoubleArrayFieldTest_primitive.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        DoubleArrayFieldTest_primitive.V0 v = new DoubleArrayFieldTest_primitive.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty);
        Assert.assertEquals("{\"value\":[]}", text);
    }

    public static class V0 {
        private double[] value;

        public double[] getValue() {
            return value;
        }

        public void setValue(double[] value) {
            this.value = value;
        }
    }
}

