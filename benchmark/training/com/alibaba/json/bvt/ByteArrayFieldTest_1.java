package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class ByteArrayFieldTest_1 extends TestCase {
    public void test_array() throws Exception {
        Assert.assertEquals("\"AQ==\"", JSON.toJSONString(new byte[]{ 1 }));
    }

    public void test_codec_null() throws Exception {
        ByteArrayFieldTest_1.V0 v = new ByteArrayFieldTest_1.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ByteArrayFieldTest_1.V0 v1 = JSON.parseObject(text, ByteArrayFieldTest_1.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        ByteArrayFieldTest_1.V0 v = new ByteArrayFieldTest_1.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty);
        Assert.assertEquals("{\"value\":[]}", text);
    }

    public static class V0 {
        private byte[] value;

        public byte[] getValue() {
            return value;
        }

        public void setValue(byte[] value) {
            this.value = value;
        }
    }
}

