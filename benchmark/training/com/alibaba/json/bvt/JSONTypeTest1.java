package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class JSONTypeTest1 extends TestCase {
    public void test_ignores() throws Exception {
        JSONTypeTest1.A a = new JSONTypeTest1.A();
        a.setF1(1001);
        a.setF2(1002);
        a.setF3(1003);
        Assert.assertEquals("{\"f1\":1001,\"f3\":1003}", JSON.toJSONString(a));
    }

    public void test_ignoresParent() throws Exception {
        JSONTypeTest1.B b = new JSONTypeTest1.B();
        b.setF1(1001);
        b.setF2(1002);
        b.setF3(1003);
        b.setF4(1004);
        b.setF5(1005);
        Assert.assertEquals("{\"f1\":1001,\"f3\":1003,\"f5\":1005}", JSON.toJSONString(b));
    }

    public void test_ignoresParent2() throws Exception {
        JSONTypeTest1.C c = new JSONTypeTest1.C();
        c.setF1(1001);
        c.setF2(1002);
        c.setF3(1003);
        c.setF4(1004);
        c.setF5(1005);
        c.setF6(1006);
        Assert.assertEquals("{\"f1\":1001,\"f3\":1003,\"f5\":1005,\"f6\":1006}", JSON.toJSONString(c));
    }

    public void test_ignoresParent3() throws Exception {
        JSONTypeTest1.D d = new JSONTypeTest1.D();
        d.setF1(1001);
        d.setF2(1002);
        d.setF3(1003);
        d.setF4(1004);
        d.setF5(1005);
        d.setF6(1006);
        d.setF7(1007);
        Assert.assertEquals("{\"f1\":1001,\"f3\":1003,\"f5\":1005,\"f6\":1006,\"f7\":1007}", JSON.toJSONString(d));
    }

    @JSONType(ignores = "f2")
    public static class A {
        private int f1;

        private int f2;

        private int f3;

        public int getF1() {
            return f1;
        }

        public void setF1(int f1) {
            this.f1 = f1;
        }

        public int getF2() {
            return f2;
        }

        public void setF2(int f2) {
            this.f2 = f2;
        }

        public int getF3() {
            return f3;
        }

        public void setF3(int f3) {
            this.f3 = f3;
        }
    }

    @JSONType(ignores = { "f4" })
    public static class B extends JSONTypeTest1.A {
        private int f4;

        private int f5;

        public int getF4() {
            return f4;
        }

        public void setF4(int f4) {
            this.f4 = f4;
        }

        public int getF5() {
            return f5;
        }

        public void setF5(int f5) {
            this.f5 = f5;
        }
    }

    public static class C extends JSONTypeTest1.B {
        private int f6;

        public int getF6() {
            return f6;
        }

        public void setF6(int f6) {
            this.f6 = f6;
        }
    }

    public static class D extends JSONTypeTest1.C {
        private int f7;

        public int getF7() {
            return f7;
        }

        public void setF7(int f7) {
            this.f7 = f7;
        }
    }
}

