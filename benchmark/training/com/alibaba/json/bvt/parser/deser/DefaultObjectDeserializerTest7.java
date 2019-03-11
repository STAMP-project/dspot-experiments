package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class DefaultObjectDeserializerTest7 extends TestCase {
    public void test_0() throws Exception {
        DefaultObjectDeserializerTest7.VO<DefaultObjectDeserializerTest7.A> vo = JSON.parseObject("{\"value\":[{\"id\":123}]}", new com.alibaba.fastjson.TypeReference<DefaultObjectDeserializerTest7.VO<DefaultObjectDeserializerTest7.A>>() {});
        DefaultObjectDeserializerTest7.A a = vo.getValue()[0];
        Assert.assertEquals(123, a.getId());
    }

    public static class VO<T> {
        private T[] value;

        public T[] getValue() {
            return value;
        }

        public void setValue(T[] value) {
            this.value = value;
        }
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

