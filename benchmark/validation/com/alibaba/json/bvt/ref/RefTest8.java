package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest8 extends TestCase {
    public void test_bug_for_juqkai() throws Exception {
        RefTest8.C c = new RefTest8.C();
        Map<String, Object> a = Collections.<String, Object>singletonMap("c", c);
        Map<String, Object> b = Collections.<String, Object>singletonMap("c", c);
        Map<String, Object> vo = new HashMap<String, Object>();
        vo.put("a", a);
        vo.put("b", b);
        Object[] root = new Object[]{ vo };
        String text = JSON.toJSONString(root);
        System.out.println(text);
        RefTest8.VO[] array2 = JSON.parseObject(text, RefTest8.VO[].class);
        Assert.assertEquals(1, array2.length);
        Assert.assertNotNull(array2[0].getA());
        Assert.assertNotNull(array2[0].getB());
        Assert.assertNotNull(array2[0].getA().getC());
        Assert.assertNotNull(array2[0].getB().getC());
        Assert.assertSame(array2[0].getA().getC(), array2[0].getB().getC());
    }

    private static class VO {
        private RefTest8.A a;

        private RefTest8.B b;

        public RefTest8.A getA() {
            return a;
        }

        public void setA(RefTest8.A a) {
            this.a = a;
        }

        public RefTest8.B getB() {
            return b;
        }

        public void setB(RefTest8.B b) {
            this.b = b;
        }
    }

    private static class A {
        private RefTest8.C c;

        public A() {
        }

        public A(RefTest8.C c) {
            this.c = c;
        }

        public RefTest8.C getC() {
            return c;
        }

        public void setC(RefTest8.C c) {
            this.c = c;
        }
    }

    private static class B {
        private RefTest8.C c;

        public B() {
        }

        public B(RefTest8.C c) {
            this.c = c;
        }

        public RefTest8.C getC() {
            return c;
        }

        public void setC(RefTest8.C c) {
            this.c = c;
        }
    }

    private static class C {
        public C() {
        }
    }
}

