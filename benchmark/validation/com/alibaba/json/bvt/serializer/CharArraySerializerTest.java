package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class CharArraySerializerTest extends TestCase {
    public void test_null() throws Exception {
        CharArraySerializerTest.VO vo = new CharArraySerializerTest.VO();
        Assert.assertEquals("{\"value\":[]}", JSON.toJSONString(vo, WriteMapNullValue, WriteNullListAsEmpty));
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(vo, WriteMapNullValue));
    }

    private static class VO {
        private char[] value;

        public char[] getValue() {
            return value;
        }

        public void setValue(char[] value) {
            this.value = value;
        }
    }
}

