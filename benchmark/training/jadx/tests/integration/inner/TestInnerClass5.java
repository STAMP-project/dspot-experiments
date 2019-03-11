package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInnerClass5 extends IntegrationTest {
    public static class TestCls {
        private String i0;

        public class A {
            protected String a;

            public A() {
                a = "";
            }

            public String a() {
                return "";
            }
        }

        public class I0 {
            private String i0;

            private String i1;

            public class I1 {
                private String i0;

                private String i1;

                private String i2;

                public I1() {
                    TestInnerClass5.TestCls.this.i0 = "i0";
                    TestInnerClass5.TestCls.I0.this.i0 = "i1";
                    TestInnerClass5.TestCls.I0.this.i1 = "i2";
                    i0 = "i0";
                    i1 = "i1";
                    i2 = "i2";
                }

                public String i() {
                    String result = (((((TestInnerClass5.TestCls.this.i0) + (TestInnerClass5.TestCls.I0.this.i0)) + (TestInnerClass5.TestCls.I0.this.i1)) + (i0)) + (i1)) + (i2);
                    TestInnerClass5.TestCls.A a = new TestInnerClass5.TestCls.A() {
                        public String a() {
                            TestInnerClass5.TestCls.this.i0 = "i1";
                            TestInnerClass5.TestCls.I0.this.i0 = "i2";
                            TestInnerClass5.TestCls.I0.this.i1 = "i3";
                            TestInnerClass5.TestCls.I0.I1.this.i0 = "i1";
                            TestInnerClass5.TestCls.I0.I1.this.i1 = "i2";
                            TestInnerClass5.TestCls.I0.I1.this.i2 = "i3";
                            a = "a";
                            return ((((((TestInnerClass5.TestCls.this.i0) + (TestInnerClass5.TestCls.I0.this.i0)) + (TestInnerClass5.TestCls.I0.this.i1)) + (TestInnerClass5.TestCls.I0.I1.this.i0)) + (TestInnerClass5.TestCls.I0.I1.this.i1)) + (TestInnerClass5.TestCls.I0.I1.this.i2)) + (a);
                        }
                    };
                    return result + (a.a());
                }
            }

            public I0() {
                TestInnerClass5.TestCls.this.i0 = "i-";
                i0 = "i0";
                i1 = "i1";
            }

            public String i() {
                String result = ((TestInnerClass5.TestCls.this.i0) + (i0)) + (i1);
                return result + (new TestInnerClass5.TestCls.I0.I1().i());
            }
        }

        public void check() throws Exception {
            Assert.assertTrue(new TestInnerClass5.TestCls.I0().i().equals("i-i0i1i0i1i2i0i1i2i1i2i3i1i2i3a"));
            Assert.assertTrue(i0.equals("i1"));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInnerClass5.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("public class I0 {"));
        Assert.assertThat(code, JadxMatchers.containsOne("public class I1 {"));
    }
}

