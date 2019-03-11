package com.alibaba.json.bvt;


import JSON.DEFAULT_PARSER_FEATURE;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        ListFieldTest.V0 v = new ListFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ParserConfig config = new ParserConfig();
        config.setAutoTypeSupport(true);
        config.setAsmEnable(false);
        ListFieldTest.V0 v1 = JSON.parseObject(text, ListFieldTest.V0.class, config, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public static class V0 {
        private List<Object> value;

        public List<Object> getValue() {
            return value;
        }

        public void setValue(List<Object> value) {
            this.value = value;
        }
    }
}

