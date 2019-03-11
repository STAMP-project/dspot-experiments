package jadx.tests.integration.inline;


import jadx.core.dex.nodes.ClassNode;
import jadx.tests.api.IntegrationTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestInline6 extends IntegrationTest {
    public static class TestCls {
        public void f() {
        }

        public void test(int a, int b) {
            long start = System.nanoTime();
            f();
            System.out.println(((System.nanoTime()) - start));
        }
    }

    @Test
    public void test() {
        ClassNode cls = getClassNode(TestInline6.TestCls.class);
        String code = cls.getCode().toString();
        Assert.assertThat(code, CoreMatchers.containsString("System.out.println(System.nanoTime() - start);"));
        Assert.assertThat(code, CoreMatchers.not(CoreMatchers.containsString("System.out.println(System.nanoTime() - System.nanoTime());")));
    }
}

