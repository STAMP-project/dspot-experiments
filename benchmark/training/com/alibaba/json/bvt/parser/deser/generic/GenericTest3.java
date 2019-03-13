package com.alibaba.json.bvt.parser.deser.generic;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class GenericTest3 extends TestCase {
    public static class A<T> {
        public GenericTest3.B<T> b;
    }

    public static class B<T> {
        public T value;
    }

    public static class ValueObject {
        public String property1;

        public int property2;
    }

    public void test_generic() throws Exception {
        GenericTest3.A<GenericTest3.ValueObject> object = JSON.parseObject("{b:{value:{property1:'string',property2:123}}}", new com.alibaba.fastjson.TypeReference<GenericTest3.A<GenericTest3.ValueObject>>() {});
        Assert.assertEquals(GenericTest3.ValueObject.class, object.b.value.getClass());
    }
}

