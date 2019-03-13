package com.alibaba.json.bvt.atomic;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.concurrent.atomic.AtomicIntegerArray;
import junit.framework.TestCase;
import org.junit.Assert;


public class AtomicIntegerArrayFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        AtomicIntegerArrayFieldTest.V0 v = new AtomicIntegerArrayFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        AtomicIntegerArrayFieldTest.V0 v1 = JSON.parseObject(text, AtomicIntegerArrayFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        AtomicIntegerArrayFieldTest.V0 v = new AtomicIntegerArrayFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty);
        Assert.assertEquals("{\"value\":[]}", text);
    }

    public void test_codec_null_2() throws Exception {
        AtomicIntegerArrayFieldTest.V0 v = JSON.parseObject("{\"value\":[1,2]}", AtomicIntegerArrayFieldTest.V0.class);
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty);
        Assert.assertEquals("{\"value\":[1,2]}", text);
    }

    public static class V0 {
        private AtomicIntegerArray value;

        public AtomicIntegerArray getValue() {
            return value;
        }

        public void setValue(AtomicIntegerArray value) {
            this.value = value;
        }
    }
}

