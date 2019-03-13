package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInner2Samples extends IntegrationTest {
    public static class TestInner2 {
        private String a;

        public class A {
            public A() {
                a = "a";
            }

            public String a() {
                return a;
            }
        }

        private static String b;

        public static class B {
            public B() {
                TestInner2Samples.TestInner2.b = "b";
            }

            public String b() {
                return TestInner2Samples.TestInner2.b;
            }
        }

        private String c;

        private void setC(String c) {
            this.c = c;
        }

        public class C {
            public String c() {
                setC("c");
                return c;
            }
        }

        private static String d;

        private static void setD(String s) {
            TestInner2Samples.TestInner2.d = s;
        }

        public static class D {
            public String d() {
                TestInner2Samples.TestInner2.setD("d");
                return TestInner2Samples.TestInner2.d;
            }
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInner2Samples.TestInner2.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("setD(\"d\");"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("synthetic")));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("access$")));
    }
}

