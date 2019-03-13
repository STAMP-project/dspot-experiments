package com.alibaba.json.bvt;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanArrayFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        BooleanArrayFieldTest.V0 v = new BooleanArrayFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        BooleanArrayFieldTest.V0 v1 = JSON.parseObject(text, BooleanArrayFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        BooleanArrayFieldTest.V0 v = new BooleanArrayFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty);
        Assert.assertEquals("{\"value\":[]}", text);
    }

    public static class V0 {
        private Boolean[] value;

        public Boolean[] getValue() {
            return value;
        }

        public void setValue(Boolean[] value) {
            this.value = value;
        }
    }
}

