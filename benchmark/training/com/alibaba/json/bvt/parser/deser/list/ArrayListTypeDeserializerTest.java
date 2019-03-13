package com.alibaba.json.bvt.parser.deser.list;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class ArrayListTypeDeserializerTest extends TestCase {
    public void test_null_0() throws Exception {
        Assert.assertNull(JSON.parseObject("null", new com.alibaba.fastjson.TypeReference<java.util.ArrayList<Integer>>() {}));
    }

    public void test_null_1() throws Exception {
        Assert.assertNull(JSON.parseObject("null", new com.alibaba.fastjson.TypeReference<java.util.Collection<Integer>>() {}));
    }

    public void test_null_2() throws Exception {
        Assert.assertNull(JSON.parseObject("{\"value\":null}", ArrayListTypeDeserializerTest.VO.class).getValue());
    }

    public void test_null_3() throws Exception {
        Assert.assertNull(JSON.parseObject("{\"value\":null}", ArrayListTypeDeserializerTest.V1.class).getValue());
    }

    public void test_empty() throws Exception {
        Assert.assertEquals(0, JSON.parseObject("[]", new com.alibaba.fastjson.TypeReference<java.util.ArrayList<Integer>>() {}).size());
        Assert.assertEquals(0, JSON.parseObject("[]", new com.alibaba.fastjson.TypeReference<java.util.Set<Integer>>() {}).size());
        Assert.assertEquals(0, JSON.parseObject("[]", new com.alibaba.fastjson.TypeReference<java.util.HashSet<Integer>>() {}).size());
        Assert.assertEquals(0, JSON.parseObject("{\"value\":[]}", ArrayListTypeDeserializerTest.VO.class).getValue().size());
    }

    public static class VO {
        private java.util.ArrayList<Integer> value;

        public java.util.ArrayList<Integer> getValue() {
            return value;
        }

        public void setValue(java.util.ArrayList<Integer> value) {
            this.value = value;
        }
    }

    private static class V1 {
        private java.util.ArrayList<Integer> value;

        public java.util.ArrayList<Integer> getValue() {
            return value;
        }

        @SuppressWarnings("unused")
        public void setValue(java.util.ArrayList<Integer> value) {
            this.value = value;
        }
    }
}

