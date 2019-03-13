package com.alibaba.json.bvt.serializer;


import SerializerFeature.WriteMapNullValue;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class IntegerArrayFieldSerializerTest extends TestCase {
    public void test_0() throws Exception {
        IntegerArrayFieldSerializerTest.A a1 = new IntegerArrayFieldSerializerTest.A();
        a1.setBytes(new int[]{ 1, 2 });
        Assert.assertEquals("{\"bytes\":[1,2]}", JSON.toJSONString(a1));
    }

    public void test_1() throws Exception {
        IntegerArrayFieldSerializerTest.A a1 = new IntegerArrayFieldSerializerTest.A();
        Assert.assertEquals("{\"bytes\":null}", JSON.toJSONString(a1, WriteMapNullValue));
    }

    public static class A {
        private int[] bytes;

        public int[] getBytes() {
            return bytes;
        }

        public void setBytes(int[] bytes) {
            this.bytes = bytes;
        }
    }
}

