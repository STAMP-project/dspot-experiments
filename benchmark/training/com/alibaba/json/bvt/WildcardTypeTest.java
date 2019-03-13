package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class WildcardTypeTest extends TestCase {
    public void test_for_wildcardType() throws Exception {
        WildcardTypeTest.TestType<WildcardTypeTest.B> b = new WildcardTypeTest.TestType<WildcardTypeTest.B>();
        b.value = new WildcardTypeTest.B(2001, "BBBB");
        b.superType = new WildcardTypeTest.TestType<WildcardTypeTest.A>(new WildcardTypeTest.A(101));
        String json = JSON.toJSONString(b);
        TestCase.assertEquals("{\"superType\":{\"value\":{\"id\":101}},\"value\":{\"id\":2001,\"name\":\"BBBB\"}}", json);
        WildcardTypeTest.TestType<WildcardTypeTest.B> b1 = JSON.parseObject(json, new com.alibaba.fastjson.TypeReference<WildcardTypeTest.TestType<WildcardTypeTest.B>>() {});
        String json2 = JSON.toJSONString(b1);
        TestCase.assertEquals(json, json2);
    }

    public static class TestType<X> {
        public X value;

        public WildcardTypeTest.TestType<? super X> superType;

        public TestType() {
        }

        public TestType(X value) {
            this.value = value;
        }
    }

    public static class TestType2<X, Y> {
        WildcardTypeTest.TestType2<? super Y, ? super X> superReversedType;
    }

    public static class A {
        public int id;

        public A(int id) {
            this.id = id;
        }
    }

    public static class B extends WildcardTypeTest.A {
        public String name;

        public B(int id, String name) {
            super(id);
            this.name = name;
        }
    }
}

