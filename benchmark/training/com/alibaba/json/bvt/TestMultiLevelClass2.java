package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;


public class TestMultiLevelClass2 extends TestCase {
    public static class A {
        private TestMultiLevelClass2.A.B b;

        public TestMultiLevelClass2.A.B getB() {
            return b;
        }

        public void setB(TestMultiLevelClass2.A.B b) {
            this.b = b;
        }

        public class B {
            private TestMultiLevelClass2.A.B.C c;

            public TestMultiLevelClass2.A.B.C getC() {
                return c;
            }

            public void setC(TestMultiLevelClass2.A.B.C c) {
                this.c = c;
            }

            class C {
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
        TestMultiLevelClass2.A a = new TestMultiLevelClass2.A();
        a.setB(a.new B());
        a.getB().setC(a.b.new C());
        a.getB().getC().setValue(123);
        String text = JSON.toJSONString(a);
        System.out.println(text);
        TestMultiLevelClass2.A a2 = JSON.parseObject(text, TestMultiLevelClass2.A.class);
    }
}

