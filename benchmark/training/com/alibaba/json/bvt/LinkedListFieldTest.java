package com.alibaba.json.bvt;


import SerializerFeature.QuoteFieldNames;
import SerializerFeature.UseSingleQuotes;
import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.LinkedList;
import junit.framework.TestCase;
import org.junit.Assert;


public class LinkedListFieldTest extends TestCase {
    public void test_codec_null() throws Exception {
        LinkedListFieldTest.V0 v = new LinkedListFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        String text = JSON.toJSONString(v, mapping, WriteMapNullValue);
        Assert.assertEquals("{\"value\":null}", text);
        LinkedListFieldTest.V0 v1 = JSON.parseObject(text, LinkedListFieldTest.V0.class);
        Assert.assertEquals(v1.getValue(), v.getValue());
    }

    public void test_codec_null_1() throws Exception {
        LinkedListFieldTest.V0 v = new LinkedListFieldTest.V0();
        SerializeConfig mapping = new SerializeConfig();
        mapping.setAsmEnable(false);
        Assert.assertEquals("{\"value\":[]}", JSON.toJSONString(v, mapping, WriteMapNullValue, WriteNullListAsEmpty));
        Assert.assertEquals("{value:[]}", JSON.toJSONStringZ(v, mapping, WriteMapNullValue, WriteNullListAsEmpty));
        Assert.assertEquals("{value:[]}", JSON.toJSONStringZ(v, mapping, UseSingleQuotes, WriteMapNullValue, WriteNullListAsEmpty));
        Assert.assertEquals("{'value':[]}", JSON.toJSONStringZ(v, mapping, UseSingleQuotes, QuoteFieldNames, WriteMapNullValue, WriteNullListAsEmpty));
    }

    public static class V0 {
        private LinkedList value;

        public LinkedList getValue() {
            return value;
        }

        public void setValue(LinkedList value) {
            this.value = value;
        }
    }
}

