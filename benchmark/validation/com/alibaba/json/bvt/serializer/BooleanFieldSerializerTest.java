package com.alibaba.json.bvt.serializer;


import JSON.DEFAULT_PARSER_FEATURE;
import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullBooleanAsFalse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanFieldSerializerTest extends TestCase {
    public void test_0() {
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(new BooleanFieldSerializerTest.Entity(), WriteMapNullValue));
        Assert.assertEquals("{\"value\":false}", JSON.toJSONString(new BooleanFieldSerializerTest.Entity(), WriteMapNullValue, WriteNullBooleanAsFalse));
    }

    public void test_codec_no_asm() throws Exception {
        BooleanFieldSerializerTest.Entity v = new BooleanFieldSerializerTest.Entity();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        BooleanFieldSerializerTest.Entity v1 = BooleanFieldSerializerTest.parseObjectNoAsm(text, BooleanFieldSerializerTest.Entity.class, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(v.getValue(), v1.getValue());
    }

    public void test_codec() throws Exception {
        BooleanFieldSerializerTest.Entity v1 = BooleanFieldSerializerTest.parseObjectNoAsm("{value:1}", BooleanFieldSerializerTest.Entity.class, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(true, v1.getValue());
    }

    public void test_codec_0() throws Exception {
        BooleanFieldSerializerTest.Entity v1 = BooleanFieldSerializerTest.parseObjectNoAsm("{value:0}", BooleanFieldSerializerTest.Entity.class, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(false, v1.getValue());
    }

    public void test_codec_1() throws Exception {
        BooleanFieldSerializerTest.Entity v1 = BooleanFieldSerializerTest.parseObjectNoAsm("{value:'true'}", BooleanFieldSerializerTest.Entity.class, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(true, v1.getValue());
    }

    public static class Entity {
        private Boolean value;

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }
}

