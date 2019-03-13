package com.alibaba.json.bvt;


import JSON.DEFAULT_PARSER_FEATURE;
import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullStringAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringBufferFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        StringBufferFieldTest.V0 v = new StringBufferFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ParserConfig config = new ParserConfig();
        config.setAsmEnable(false);
        StringBufferFieldTest.V0 v1 = JSON.parseObject(text, StringBufferFieldTest.V0.class, config, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        StringBufferFieldTest.V0 v = new StringBufferFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullStringAsEmpty);
        Assert.assertEquals("{\"value\":\"\"}", text);
    }

    public void test_deserialize_1() throws Exception {
        String json = "{\"value\":\"\"}";
        StringBufferFieldTest.V0 vo = JSON.parseObject(json, StringBufferFieldTest.V0.class);
        Assert.assertNotNull(vo.getValue());
        Assert.assertEquals("", vo.getValue().toString());
    }

    public void test_deserialize_2() throws Exception {
        String json = "{\"value\":null}";
        StringBufferFieldTest.V0 vo = JSON.parseObject(json, StringBufferFieldTest.V0.class);
        Assert.assertNull(vo.getValue());
    }

    public void test_deserialize_3() throws Exception {
        String json = "{\"value\":\"true\"}";
        StringBufferFieldTest.V0 vo = JSON.parseObject(json, StringBufferFieldTest.V0.class);
        Assert.assertNotNull(vo.getValue());
        Assert.assertEquals("true", vo.getValue().toString());
    }

    public void test_deserialize_4() throws Exception {
        String json = "{\"value\":\"123\"}";
        StringBufferFieldTest.V0 vo = JSON.parseObject(json, StringBufferFieldTest.V0.class);
        Assert.assertNotNull(vo.getValue());
        Assert.assertEquals("123", vo.getValue().toString());
    }

    public static class V0 {
        private StringBuffer value;

        public StringBuffer getValue() {
            return value;
        }

        public void setValue(StringBuffer value) {
            this.value = value;
        }
    }
}

