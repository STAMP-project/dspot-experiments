package com.alibaba.json.bvt.parser.deser.list;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayDeserializerTest extends TestCase {
    public void test_null() throws Exception {
        Assert.assertNull(JSON.parseObject("null", Object[].class));
        Assert.assertNull(JSON.parseObject("null", String[].class));
        Assert.assertNull(JSON.parseObject("null", ArrayDeserializerTest.VO[].class));
        Assert.assertNull(JSON.parseObject("null", ArrayDeserializerTest.VO[][].class));
    }

    public void test_0() throws Exception {
        Assert.assertEquals(0, JSON.parseObject("[]", Object[].class).length);
        Assert.assertEquals(0, JSON.parseObject("[]", Object[][].class).length);
        Assert.assertEquals(0, JSON.parseObject("[]", Object[][][].class).length);
        Assert.assertEquals(1, JSON.parseObject("[null]", Object[].class).length);
        Assert.assertEquals(1, JSON.parseObject("[null]", Object[][].class).length);
        Assert.assertEquals(1, JSON.parseObject("[[[[[[]]]]]]", Object[][].class).length);
        Assert.assertEquals(1, JSON.parseObject("[null]", Object[][][].class).length);
        Assert.assertEquals(null, JSON.parseObject("{\"value\":null}", ArrayDeserializerTest.VO.class).getValue());
    }

    public static class VO {
        private Object[] value;

        public Object[] getValue() {
            return value;
        }

        public void setValue(Object[] value) {
            this.value = value;
        }
    }
}

