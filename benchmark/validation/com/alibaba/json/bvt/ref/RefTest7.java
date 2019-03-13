package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest7 extends TestCase {
    public void test_bug_for_juqkai() throws Exception {
        RefTest7.VO vo = new RefTest7.VO();
        RefTest7.C c = new RefTest7.C();
        vo.setA(new RefTest7.A(c));
        vo.setB(new RefTest7.B(c));
        RefTest7.VO[] root = new RefTest7.VO[]{ vo };
        String text = JSON.toJSONString(root);
        System.out.println(text);
        RefTest7.VO[] array2 = JSON.parseObject(text, RefTest7.VO[].class);
        Assert.assertEquals(1, array2.length);
        Assert.assertNotNull(array2[0].getA());
        Assert.assertNotNull(array2[0].getB());
        Assert.assertNotNull(array2[0].getA().getC());
        Assert.assertNotNull(array2[0].getB().getC());
        Assert.assertSame(array2[0].getA().getC(), array2[0].getB().getC());
    }

    public static class VO {
        private RefTest7.A a;

        private RefTest7.B b;

        public RefTest7.A getA() {
            return a;
        }

        public void setA(RefTest7.A a) {
            this.a = a;
        }

        public RefTest7.B getB() {
            return b;
        }

        public void setB(RefTest7.B b) {
            this.b = b;
        }
    }

    public static class A {
        private RefTest7.C c;

        public A() {
        }

        public A(RefTest7.C c) {
            this.c = c;
        }

        public RefTest7.C getC() {
            return c;
        }

        public void setC(RefTest7.C c) {
            this.c = c;
        }
    }

    public static class B {
        private RefTest7.C c;

        public B() {
        }

        public B(RefTest7.C c) {
            this.c = c;
        }

        public RefTest7.C getC() {
            return c;
        }

        public void setC(RefTest7.C c) {
            this.c = c;
        }
    }

    public static class C {
        public C() {
        }
    }
}

