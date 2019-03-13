package com.alibaba.json.bvt.serializer;


import SerializerFeature.UseISO8601DateFormat;
import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class CircularReferencesTest extends TestCase {
    public void test_0() throws Exception {
        CircularReferencesTest.A a = new CircularReferencesTest.A();
        CircularReferencesTest.B b = new CircularReferencesTest.B(a);
        a.setB(b);
        String text = JSON.toJSONString(a);
        CircularReferencesTest.A a1 = JSON.parseObject(text, CircularReferencesTest.A.class);
        Assert.assertTrue((a1 == (a1.getB().getA())));
    }

    public void test_1() throws Exception {
        CircularReferencesTest.A a = new CircularReferencesTest.A();
        CircularReferencesTest.B b = new CircularReferencesTest.B(a);
        a.setB(b);
        String text = JSON.toJSONString(a, UseISO8601DateFormat);
        CircularReferencesTest.A a1 = JSON.parseObject(text, CircularReferencesTest.A.class);
        Assert.assertTrue((a1 == (a1.getB().getA())));
    }

    public void test_2() throws Exception {
        CircularReferencesTest.A a = new CircularReferencesTest.A();
        CircularReferencesTest.B b = new CircularReferencesTest.B(a);
        a.setB(b);
        String text = JSON.toJSONString(a, true);
        CircularReferencesTest.A a1 = JSON.parseObject(text, CircularReferencesTest.A.class);
        Assert.assertTrue((a1 == (a1.getB().getA())));
    }

    public static class A {
        private CircularReferencesTest.B b;

        public A() {
        }

        public A(CircularReferencesTest.B b) {
            this.b = b;
        }

        public CircularReferencesTest.B getB() {
            return b;
        }

        public void setB(CircularReferencesTest.B b) {
            this.b = b;
        }
    }

    public static class B {
        private CircularReferencesTest.A a;

        public B() {
        }

        public B(CircularReferencesTest.A a) {
            this.a = a;
        }

        public CircularReferencesTest.A getA() {
            return a;
        }

        public void setA(CircularReferencesTest.A a) {
            this.a = a;
        }
    }
}

