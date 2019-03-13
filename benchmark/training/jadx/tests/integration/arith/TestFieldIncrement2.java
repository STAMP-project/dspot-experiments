package jadx.tests.integration.arith;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestFieldIncrement2 extends IntegrationTest {
    public static class TestCls {
        private static class A {
            int f = 5;
        }

        public TestFieldIncrement2.TestCls.A a;

        public void test1(int n) {
            this.a.f = (this.a.f) + n;
        }

        public void test2(int n) {
            this.a.f *= n;
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestFieldIncrement2.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("this.a.f += n;"));
        Assert.assertThat(code, CoreMatchers.containsString("a.f *= n;"));
        // TODO
        // assertThat(code, containsString("this.a.f *= n;"));
    }
}

