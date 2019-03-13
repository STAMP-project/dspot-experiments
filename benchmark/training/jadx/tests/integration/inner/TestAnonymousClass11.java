package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass11 extends IntegrationTest {
    public static class TestCls {
        public void test() {
            final int a = new Random().nextInt();
            final long l = new Random().nextLong();
            func(new TestAnonymousClass11.TestCls.A(l) {
                @Override
                public void m() {
                    System.out.println(a);
                }
            });
            System.out.println(("a" + a));
            print(a);
            print2(1, a);
            print3(1, l);
        }

        public abstract class A {
            public A(long l) {
            }

            public abstract void m();
        }

        private void func(TestAnonymousClass11.TestCls.A a) {
        }

        private void print(int a) {
        }

        private void print2(int i, int a) {
        }

        private void print3(int i, long l) {
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass11.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("System.out.println(\"a\" + a);"));
        Assert.assertThat(code, JadxMatchers.containsOne("print(a);"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("synthetic")));
    }
}

