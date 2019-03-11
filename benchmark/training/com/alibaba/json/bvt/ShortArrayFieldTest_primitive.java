package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class ShortArrayFieldTest_primitive extends TestCase {
    public void test_array() throws Exception {
        Assert.assertEquals("[1]", JSON.toJSONString(new short[]{ 1 }));
    }

    public void test_codec_null() throws Exception {
        ShortArrayFieldTest_primitive.V0 v = new ShortArrayFieldTest_primitive.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ShortArrayFieldTest_primitive.V0 v1 = JSON.parseObject(text, ShortArrayFieldTest_primitive.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        ShortArrayFieldTest_primitive.V0 v = new ShortArrayFieldTest_primitive.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty);
        Assert.assertEquals("{\"value\":[]}", text);
    }

    public static class V0 {
        private short[] value;

        public short[] getValue() {
            return value;
        }

        public void setValue(short[] value) {
            this.value = value;
        }
    }
}

