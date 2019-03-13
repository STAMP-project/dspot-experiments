package jadx.tests.integration.inner;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import jadx.tests.api.utils.JadxMatchers;
import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TestAnonymousClass10 extends IntegrationTest {
    public static class TestCls {
        public TestAnonymousClass10.TestCls.A test() {
            Random random = new Random();
            int a2 = random.nextInt();
            int a3 = a2 + 3;
            return new TestAnonymousClass10.TestCls.A(this, a2, a3, 4, 5, random.nextDouble()) {
                @Override
                public void m() {
                    System.out.println(1);
                }
            };
        }

        public abstract class A {
            public A(TestAnonymousClass10.TestCls a1, int a2, int a3, int a4, int a5, double a6) {
            }

            public abstract void m();
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestAnonymousClass10.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, JadxMatchers.containsOne("return new A(this, a2, a2 + 3, 4, 5, random.nextDouble()) {"));
        Assert.assertThat(code, Matchers.not(Matchers.containsString("synthetic")));
    }
}

