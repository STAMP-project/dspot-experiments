package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ByteArrayFieldSerializerTest extends TestCase {
    public void test_0() throws Exception {
        ByteArrayFieldSerializerTest.A a1 = new ByteArrayFieldSerializerTest.A();
        a1.setBytes(new byte[]{ 1, 2 });
        Assert.assertEquals("{\"bytes\":\"AQI=\"}", JSON.toJSONString(a1));
    }

    public void test_1() throws Exception {
        ByteArrayFieldSerializerTest.A a1 = new ByteArrayFieldSerializerTest.A();
        Assert.assertEquals("{\"bytes\":null}", JSON.toJSONString(a1, WriteMapNullValue));
    }

    public static class A {
        private byte[] bytes;

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }
}

