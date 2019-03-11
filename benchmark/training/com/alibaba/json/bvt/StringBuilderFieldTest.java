package com.alibaba.json.bvt;


import JSON.DEFAULT_PARSER_FEATURE;
import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullStringAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringBuilderFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        StringBuilderFieldTest.V0 v = new StringBuilderFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ParserConfig config = new ParserConfig();
        config.setAsmEnable(false);
        StringBuilderFieldTest.V0 v1 = JSON.parseObject(text, StringBuilderFieldTest.V0.class, config, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        StringBuilderFieldTest.V0 v = new StringBuilderFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullStringAsEmpty);
        Assert.assertEquals("{\"value\":\"\"}", text);
    }

    public void test_deserialize_1() throws Exception {
        String json = "{\"value\":\"\"}";
        StringBuilderFieldTest.V0 vo = JSON.parseObject(json, StringBuilderFieldTest.V0.class);
        Assert.assertNotNull(vo.getValue());
        Assert.assertEquals("", vo.getValue().toString());
    }

    public void test_deserialize_2() throws Exception {
        String json = "{\"value\":null}";
        StringBuilderFieldTest.V0 vo = JSON.parseObject(json, StringBuilderFieldTest.V0.class);
        Assert.assertNull(vo.getValue());
    }

    public void test_deserialize_3() throws Exception {
        String json = "{\"value\":\"true\"}";
        StringBuilderFieldTest.V0 vo = JSON.parseObject(json, StringBuilderFieldTest.V0.class);
        Assert.assertNotNull(vo.getValue());
        Assert.assertEquals("true", vo.getValue().toString());
    }

    public void test_deserialize_4() throws Exception {
        String json = "{\"value\":\"123\"}";
        StringBuilderFieldTest.V0 vo = JSON.parseObject(json, StringBuilderFieldTest.V0.class);
        Assert.assertNotNull(vo.getValue());
        Assert.assertEquals("123", vo.getValue().toString());
    }

    public static class V0 {
        private StringBuilder value;

        public StringBuilder getValue() {
            return value;
        }

        public void setValue(StringBuilder value) {
            this.value = value;
        }
    }
}

