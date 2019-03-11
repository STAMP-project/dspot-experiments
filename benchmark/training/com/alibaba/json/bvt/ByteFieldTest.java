package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullNumberAsZero;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class ByteFieldTest extends TestCase {
    public void test_codec() throws Exception {
        ByteFieldTest.V0 v = new ByteFieldTest.V0();
        v.setValue(((byte) (10)));
        String text = JSON.toJSONString(v);
        System.out.println(text);
        ByteFieldTest.V0 v1 = JSON.parseObject(text, ByteFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null() throws Exception {
        ByteFieldTest.V0 v = new ByteFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ByteFieldTest.V0 v1 = JSON.parseObject(text, ByteFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_asm() throws Exception {
        ByteFieldTest.V0 v = new ByteFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(true);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ByteFieldTest.V0 v1 = JSON.parseObject(text, ByteFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        ByteFieldTest.V0 v = new ByteFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullNumberAsZero);
        Assert.assertEquals("{\"value\":0}", text);
        ByteFieldTest.V0 v1 = JSON.parseObject(text, ByteFieldTest.V0.class);
        Assert.assertEquals(Byte.valueOf(((byte) (0))), v1.getValue());
    }

    public static class V0 {
        private Byte value;

        public Byte getValue() {
            return value;
        }

        public void setValue(Byte value) {
            this.value = value;
        }
    }
}

