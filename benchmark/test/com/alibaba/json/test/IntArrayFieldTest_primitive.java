package com.alibaba.json.test;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class IntArrayFieldTest_primitive extends TestCase {
    public void test_array() throws Exception {
        Assert.assertEquals("[1]", JSON.toJSONString(new int[]{ 1 }));
    }

    public void test_codec_null() throws Exception {
        IntArrayFieldTest_primitive.V0 v = new IntArrayFieldTest_primitive.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        IntArrayFieldTest_primitive.V0 v1 = JSON.parseObject(text, IntArrayFieldTest_primitive.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        IntArrayFieldTest_primitive.V0 v = new IntArrayFieldTest_primitive.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        Assert.assertTrue((!(mapping.isAsmEnable())));
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty);
        Assert.assertEquals("{\"value\":[]}", text);
    }

    public static class V0 {
        private int[] value;

        public int[] getValue() {
            return value;
        }

        public void setValue(int[] value) {
            this.value = value;
        }
    }
}

