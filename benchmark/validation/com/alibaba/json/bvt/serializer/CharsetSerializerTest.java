package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import java.nio.charset.Charset;
import junit.framework.TestCase;
import org.junit.Assert;


public class CharsetSerializerTest extends TestCase {
    public void test_0() {
        Assert.assertEquals("{\"value\":null}", JSON.toJSONString(new CharsetSerializerTest.Entity(), WriteMapNullValue));
    }

    public static class Entity {
        private Charset value;

        public Charset getValue() {
            return value;
        }

        public void setValue(Charset value) {
            this.value = value;
        }
    }
}

