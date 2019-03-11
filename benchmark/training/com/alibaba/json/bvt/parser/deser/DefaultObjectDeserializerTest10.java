package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class DefaultObjectDeserializerTest10 extends TestCase {
    public <T extends DefaultObjectDeserializerTest10.A> void test_1() throws Exception {
        T[] list = JSON.parseObject("[{}]", new com.alibaba.fastjson.TypeReference<T[]>() {});
        Assert.assertEquals(1, list.length);
        Assert.assertNotNull(list[0]);
        Assert.assertTrue(((list[0]) instanceof DefaultObjectDeserializerTest10.A));
    }

    public static class A {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}

