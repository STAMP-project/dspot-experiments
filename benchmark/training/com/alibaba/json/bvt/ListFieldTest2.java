package com.alibaba.json.bvt;


import JSON.DEFAULT_PARSER_FEATURE;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.List;
import junit.framework.TestCase;
import org.junit.Assert;


public class ListFieldTest2 extends TestCase {
    public void test_codec_null() throws Exception {
        ListFieldTest2.V0 v = new ListFieldTest2.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ParserConfig config = new ParserConfig();
        config.setAsmEnable(false);
        ListFieldTest2.V0 v1 = JSON.parseObject(text, ListFieldTest2.V0.class, config, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    private static class V0 {
        private List<Object> value;

        public List<Object> getValue() {
            return value;
        }

        public void setValue(List<Object> value) {
            this.value = value;
        }
    }
}

