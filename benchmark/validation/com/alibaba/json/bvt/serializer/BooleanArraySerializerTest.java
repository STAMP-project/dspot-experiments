package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import SerializerFeature.WriteNullListAsEmpty;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class BooleanArraySerializerTest extends TestCase {
    public void test_0() {
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(new BooleanArraySerializerTest.Entity(), WriteMapNullValue));
        Assert.assertEquals("{\"value\":[]}", JSON.toJSONString(new BooleanArraySerializerTest.Entity(), WriteMapNullValue, WriteNullListAsEmpty));
    }

    public static class Entity {
        private boolean[] value;

        public boolean[] getValue() {
            return value;
        }

        public void setValue(boolean[] value) {
            this.value = value;
        }
    }
}

