package com.alibaba.json.bvt;


import JSON.DEFAULT_PARSER_FEATURE;
import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullStringAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class StringFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        StringFieldTest.V0 v = new StringFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ParserConfig config = new ParserConfig();
        config.setAsmEnable(false);
        StringFieldTest.V0 v1 = JSON.parseObject(text, StringFieldTest.V0.class, config, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        StringFieldTest.V0 v = new StringFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullStringAsEmpty);
        Assert.assertEquals("{\"value\":\"\"}", text);
    }

    public static class V0 {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

