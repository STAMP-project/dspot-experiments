package com.alibaba.json.bvt.serializer;


import JSON.DEFAULT_PARSER_FEATURE;
import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullBooleanAsFalse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanFieldSerializerTest_primitive extends TestCase {
    public void test_0() {
        Assert.assertEquals("{\"value\":false}", JSON.toJSONString(new BooleanFieldSerializerTest_primitive.Entity(), WriteMapNullValue, WriteNullBooleanAsFalse));
    }

    public void test_codec_no_asm() throws Exception {
        BooleanFieldSerializerTest_primitive.Entity v = new BooleanFieldSerializerTest_primitive.Entity();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":false}", text);
        BooleanFieldSerializerTest_primitive.Entity v1 = JSON.parseObject(text, BooleanFieldSerializerTest_primitive.Entity.class);
        Assert.assertEquals(v.getValue(), v1.getValue());
    }

    public void test_codec() throws Exception {
        BooleanFieldSerializerTest_primitive.Entity v1 = BooleanFieldSerializerTest_primitive.parseObjectNoAsm("{value:1}", BooleanFieldSerializerTest_primitive.Entity.class, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(true, v1.getValue());
    }

    public void test_codec_0() throws Exception {
        BooleanFieldSerializerTest_primitive.Entity v1 = BooleanFieldSerializerTest_primitive.parseObjectNoAsm("{value:0}", BooleanFieldSerializerTest_primitive.Entity.class, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(false, v1.getValue());
    }

    public void test_codec_1() throws Exception {
        BooleanFieldSerializerTest_primitive.Entity v1 = BooleanFieldSerializerTest_primitive.parseObjectNoAsm("{value:'true'}", BooleanFieldSerializerTest_primitive.Entity.class, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(true, v1.getValue());
    }

    public void test_codec_2() throws Exception {
        BooleanFieldSerializerTest_primitive.Entity v1 = BooleanFieldSerializerTest_primitive.parseObjectNoAsm("{value:null}", BooleanFieldSerializerTest_primitive.Entity.class, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(false, v1.getValue());
    }

    public void test_codec_3() throws Exception {
        BooleanFieldSerializerTest_primitive.Entity v1 = BooleanFieldSerializerTest_primitive.parseObjectNoAsm("{value:\"\"}", BooleanFieldSerializerTest_primitive.Entity.class, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(false, v1.getValue());
    }

    public static class Entity {
        private boolean value;

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }
}

