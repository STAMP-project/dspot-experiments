package com.alibaba.json.bvt;


import JSON.DEFAULT_PARSER_FEATURE;
import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayListFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        ArrayListFieldTest.V0 v = new ArrayListFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        ParserConfig config = new ParserConfig();
        config.setAsmEnable(false);
        ArrayListFieldTest.V0 v1 = JSON.parseObject(text, ArrayListFieldTest.V0.class, config, DEFAULT_PARSER_FEATURE);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    private static class V0 {
        private ArrayList<Object> value;

        public ArrayList<Object> getValue() {
            return value;
        }

        public void setValue(ArrayList<Object> value) {
            this.value = value;
        }
    }
}

