package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class CharArrayDeserializerTest extends TestCase {
    public void test_charArray() throws Exception {
        Assert.assertEquals(null, JSON.parseObject("{}", CharArrayDeserializerTest.VO.class).getValue());
        Assert.assertEquals(null, JSON.parseObject("{value:null}", CharArrayDeserializerTest.VO.class).getValue());
        Assert.assertEquals(null, JSON.parseObject("{'value':null}", CharArrayDeserializerTest.VO.class).getValue());
        Assert.assertEquals(null, JSON.parseObject("{\"value\":null}", CharArrayDeserializerTest.VO.class).getValue());
        Assert.assertEquals(0, JSON.parseObject("{\"value\":\"\"}", CharArrayDeserializerTest.VO.class).getValue().length);
        Assert.assertEquals(2, JSON.parseObject("{\"value\":\"ab\"}", CharArrayDeserializerTest.VO.class).getValue().length);
        Assert.assertEquals("ab", new String(JSON.parseObject("{\"value\":\"ab\"}", CharArrayDeserializerTest.VO.class).getValue()));
        Assert.assertEquals("12", new String(JSON.parseObject("{\"value\":12}", CharArrayDeserializerTest.VO.class).getValue()));
        Assert.assertEquals("12", new String(JSON.parseObject("{\"value\":12L}", CharArrayDeserializerTest.VO.class).getValue()));
        Assert.assertEquals("12", new String(JSON.parseObject("{\"value\":12S}", CharArrayDeserializerTest.VO.class).getValue()));
        Assert.assertEquals("12", new String(JSON.parseObject("{\"value\":12B}", CharArrayDeserializerTest.VO.class).getValue()));
        Assert.assertEquals("{}", new String(JSON.parseObject("{\"value\":{}}", CharArrayDeserializerTest.VO.class).getValue()));
    }

    public static class VO {
        private char[] value;

        public char[] getValue() {
            return value;
        }

        public void setValue(char[] value) {
            this.value = value;
        }
    }
}

