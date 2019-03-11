package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class TestMultiLevelClass extends TestCase {
    public static class A {
        private TestMultiLevelClass.A.B b;

        public TestMultiLevelClass.A.B getB() {
            return b;
        }

        public void setB(TestMultiLevelClass.A.B b) {
            this.b = b;
        }

        public static class B {
            private TestMultiLevelClass.A.B.C c;

            public TestMultiLevelClass.A.B.C getC() {
                return c;
            }

            public void setC(TestMultiLevelClass.A.B.C c) {
                this.c = c;
            }

            static class C {
                private int value;

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }
            }
        }
    }

    public void test_codec() throws Exception {
        TestMultiLevelClass.A a = new TestMultiLevelClass.A();
        a.setB(new TestMultiLevelClass.A.B());
        a.getB().setC(new TestMultiLevelClass.A.B.C());
        a.getB().getC().setValue(123);
        String text = JSON.toJSONString(a);
        System.out.println(text);
        TestMultiLevelClass.A a2 = JSON.parseObject(text, TestMultiLevelClass.A.class);
    }
}

