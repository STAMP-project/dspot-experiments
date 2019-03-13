package com.alibaba.json.bvt.ref;


import SerializerFeature.PrettyFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;


public class RefTest6 extends TestCase {
    /**
     * A -> B -> C -> B -> A
     *
     * @throws Exception
     * 		
     */
    public void test_0() throws Exception {
        RefTest6.A a = new RefTest6.A();
        RefTest6.B b = new RefTest6.B();
        RefTest6.C c = new RefTest6.C();
        a.setB(b);
        b.setC(c);
        c.setB(b);
        b.setA(a);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("a", a);
        jsonObject.put("c", c);
        String text = JSON.toJSONString(jsonObject, PrettyFormat);
        System.out.println(text);
    }

    private class A {
        private RefTest6.B b;

        public RefTest6.B getB() {
            return b;
        }

        public void setB(RefTest6.B b) {
            this.b = b;
        }
    }

    private class B {
        private RefTest6.C c;

        private RefTest6.A a;

        public RefTest6.C getC() {
            return c;
        }

        public void setC(RefTest6.C c) {
            this.c = c;
        }

        public RefTest6.A getA() {
            return a;
        }

        public void setA(RefTest6.A a) {
            this.a = a;
        }
    }

    private class C {
        private RefTest6.B b;

        public RefTest6.B getB() {
            return b;
        }

        public void setB(RefTest6.B b) {
            this.b = b;
        }
    }
}

